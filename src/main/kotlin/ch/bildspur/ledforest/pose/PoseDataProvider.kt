package ch.bildspur.ledforest.pose

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.ml.PoseClassifier
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.image.ImageFlip
import ch.bildspur.ledforest.model.image.ImageRotation
import ch.bildspur.ledforest.model.math.PVector4
import ch.bildspur.ledforest.pose.clients.PoseClient
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.format
import ch.bildspur.ledforest.util.toFloat2
import ch.bildspur.math.Float2
import ch.bildspur.math.Float3
import ch.bildspur.model.DataModel
import ch.bildspur.tracking.simple.SimpleTracker
import processing.core.PApplet
import processing.core.PConstants.CENTER
import processing.core.PGraphics
import processing.core.PVector
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread


/**
 * Does the pose tracking!
 */
class PoseDataProvider(val sketch: PApplet, val project: DataModel<Project>) {
    lateinit var client: PoseClient
    lateinit var trackerThread: Thread

    val poseClassifier = PoseClassifier()

    val isRunning = AtomicBoolean()

    val totalPoseCount = AtomicInteger(0)

    private val simpleTracker = SimpleTracker<Pose>(
        { it.neck.toFloat2() },
        onUpdate = { e, i ->
            // just update the keypoints but keep the object
            e.item.keypoints = i.keypoints
            e.item.score = i.score
        },
        onAdd = {
            // set initial easing position
            it.item.easedPosition.init(it.item.position, project.value.poseInteraction.positionEasing.value)
            totalPoseCount.incrementAndGet()
        },
        maxDelta = project.value.poseInteraction.maxDelta.value,
        maxUntrackedTime = project.value.poseInteraction.maxDeadTime.value
    )

    private val relevantPoses = AtomicReference<List<Pose>>(listOf())

    val poses: List<Pose>
        get() = relevantPoses.get()

    private var poseBuffer = emptyList<Pose>()

    var lastReceiveTimeStamp = 0L

    init {
        project.value.poseInteraction.maxDeadTime.onChanged += {
            simpleTracker.maxUntrackedTime = it
        }
    }

    fun start() {
        if (isRunning.get()) return

        println("pose client starting up...")

        client = project.value.poseInteraction.poseClient.value.client
        client.onPosesReceived += { rawPoses ->
            // fix raw poses
            val config = Sketch.instance.project.value.poseInteraction
            rawPoses.forEach { pose ->
                pose.keypoints.forEach {
                    val t = transform2DPoint(
                        Float2(it.x, it.y),
                        project.value.poseInteraction.imageRotation.value,
                        project.value.poseInteraction.imageFlip.value
                    )

                    it.x = t.x
                    it.y = t.y

                    it.x = if (config.flipX.value) 1f - it.x else it.x
                    it.y = if (config.flipY.value) 1f - it.y else it.y
                    it.z = if (config.flipZ.value) 1f - it.z else it.z
                }
            }

            poseBuffer = rawPoses
            lastReceiveTimeStamp = System.currentTimeMillis()
        }
        client.start(project.value.poseInteraction.port.value)

        trackerThread = thread(start = true) {
            isRunning.set(true)

            while (isRunning.get()) {
                val ts = System.currentTimeMillis();
                val lastPoses = relevantPoses.get()
                val detectedPoses = poseBuffer.filter { it.score >= project.value.poseInteraction.minScore.value }
                var actualPoses = detectedPoses

                // center xy
                if (project.value.poseInteraction.centerPoseX.value || project.value.poseInteraction.centerPoseY.value) {
                    actualPoses.forEach {
                        val center = it.bodyCenter
                        it.keypoints.forEach { kp ->
                            if (project.value.poseInteraction.centerPoseX.value)
                                kp.x -= center.x

                            if (project.value.poseInteraction.centerPoseY.value)
                                kp.y -= center.y
                        }
                    }
                }

                if (project.value.poseInteraction.useTracking.value) {
                    // run tracking
                    simpleTracker.maxDelta = project.value.poseInteraction.maxDelta.value
                    simpleTracker.track(detectedPoses)

                    // update id's and easing
                    simpleTracker.entities.forEach {
                        it.item.id = it.trackingId

                        // update easing
                        it.item.easedPosition.target.set(it.item.position)
                        it.item.easedPosition.easing = project.value.poseInteraction.positionEasing.value
                        it.item.easedPosition.update()

                        it.item.smoothRightWrist.target.set(it.item.rightWrist)
                        it.item.smoothRightWrist.easing = project.value.poseInteraction.positionEasing.value
                        it.item.smoothRightWrist.update()

                        it.item.smoothLeftWrist.target.set(it.item.leftWrist)
                        it.item.smoothLeftWrist.easing = project.value.poseInteraction.positionEasing.value
                        it.item.smoothLeftWrist.update()
                    }

                    // update poses (make this once in the loop)
                    actualPoses = simpleTracker.entities.map { it.item }
                        .filter { ts - it.startTimestamp > project.value.poseInteraction.minAliveTime.value }
                        .toList()

                    // update ui
                    project.value.poseInteraction.poseCount.value = "${simpleTracker.entities.size}"
                } else {
                    project.value.poseInteraction.poseCount.value = "${detectedPoses.size}"
                }

                // detect velocity in m/s
                if (project.value.poseInteraction.trackVelocity.value) {
                    val lastPoseTable = lastPoses.associateBy { it.id }
                    for (pose in actualPoses) {
                        val lastPose = lastPoseTable[pose.id] ?: continue
                        for (i in 0 until pose.keypoints.size) {
                            val delta = PVector.dist(pose.keypoints[i], lastPose.keypoints[i])
                            val velocity = delta / (ts / 1000f)
                            pose.keypoints[i].velocity = velocity
                        }
                    }
                }

                // classify
                val classificationConfig = project.value.poseInteraction.classification
                if (classificationConfig.enabled.value) {
                    actualPoses.forEach {
                        if (classificationConfig.sample.value) {
                            classificationConfig.sample(it, classificationConfig.label.value)
                            classificationConfig.sampleCountText.value = "${classificationConfig.sampleCount}"
                        } else {
                            val result = poseClassifier.predict(it)
                            it.classification = result.label
                            // println("${System.currentTimeMillis()} Class: ${it.classification}")
                        }
                    }
                }

                // update poses
                relevantPoses.set(actualPoses)

                // reset buffer if nothing received
                if (ts - lastReceiveTimeStamp >= project.value.poseInteraction.maxReceiveTimeout.value) {
                    poseBuffer = emptyList()
                    lastReceiveTimeStamp = ts
                }

                Thread.sleep(1000 / project.value.poseInteraction.trackingFPS.value)
            }
        }
    }

    fun stop() {
        if (!isRunning.get()) return

        println("pose client stopping...")
        client.close()

        isRunning.set(false)
        trackerThread.join(5000)
    }

    private fun transform2DPoint(pos: Float2, rotation: ImageRotation, flip: ImageFlip): Float2 {
        var nx = pos.x
        var ny = pos.y

        when (rotation) {
            ImageRotation.Clockwise90 -> {
                nx = pos.y
                ny = 1.0f - pos.x
            }
            ImageRotation.CounterClockwise90 -> {
                nx = 1.0f - pos.y
                ny = pos.x
            }
            ImageRotation.Full180 -> {
                nx = 1.0f - pos.x
                ny = 1.0f - pos.y
            }
            else -> {}
        }

        if (flip == ImageFlip.Horizontal) {
            nx = 1.0f - nx
        } else if (flip == ImageFlip.Vertical) {
            ny = 1.0f - ny
        }

        return Float2(nx, ny)
    }

    fun renderDebug(g: PGraphics) {
        if (!isRunning.get())
            return

        g.background(0f, 100f)

        // render tracked poses
        if (project.value.poseInteraction.showTrackedPoses.value) {
            val psTracked = poses
            val ias = Sketch.instance.project.value.interaction.interactionSpace.value
            for (pose in psTracked) {
                g.noStroke()
                g.fill(360.0f * (pose.id % 10) / 10.0f, 80f, 100f)
                pose.keypoints.forEach {
                    val v = PVector(
                        PApplet.map(it.x, 0f, ias.x, 0f, 1f),
                        PApplet.map(it.y, 0f, ias.y, 0f, 1f),
                        PApplet.map(it.z, 0f, ias.z, 0f, 1f)
                    )

                    g.circle(v.x * g.width, v.y * g.height, 20f)
                }

                g.stroke(255)
                g.rectMode(CENTER)
                g.square(pose.easedPosition.x * g.width, pose.easedPosition.y * g.height, 20f)

                g.fill(ColorMode.color(255))
                g.textSize(15f)
                g.text("#${pose.id} | ${pose.score.format(2)}", pose.neck.x * g.width + 10, pose.neck.y * g.height + 10)
            }
        }

        // render raw poses
        if (project.value.poseInteraction.showRawPoses.value) {
            val validPoses = poseBuffer.filter { it.score >= project.value.poseInteraction.minScore.value }
            for (pose in validPoses) {
                g.noFill()
                g.stroke(360.0f * pose.id / validPoses.size, 80f, 100f)
                pose.keypoints.forEach {
                    g.circle(it.x * g.width, it.y * g.height, 25f)
                }

                g.stroke(ColorMode.color(255))
                g.strokeWeight(2f)
                val maxDelta = project.value.poseInteraction.maxDelta.value
                g.ellipse(
                    pose.position.x * g.width,
                    pose.position.y * g.height,
                    g.width * maxDelta,
                    g.height * maxDelta
                )
            }
        }
    }
}