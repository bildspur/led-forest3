package ch.bildspur.ledforest.pose

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.format
import ch.bildspur.ledforest.util.toFloat2
import ch.bildspur.model.DataModel
import ch.bildspur.simple.SimpleTracker
import processing.core.PApplet
import processing.core.PConstants.CENTER
import processing.core.PGraphics
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread


/**
 * Does the pose tracking!
 */
class PoseDataProvider(val sketch: PApplet, val project: DataModel<Project>) {
    lateinit var client: PoseClient
    lateinit var trackerThread: Thread

    var isRunning = AtomicBoolean()

    private val simpleTracker = SimpleTracker<Pose>({ it.neck.toFloat2() },
            onUpdate = { e, i ->
                // just update the keypoints but keep the object
                e.item.keypoints = i.keypoints
                e.item.score = i.score
            },
            onAdd = {
                // set initial easing position
                it.item.easedPosition.init(it.item.position, project.value.poseInteraction.positionEasing.value)
            },
            maxDelta = project.value.poseInteraction.maxDelta.value,
            maxUntrackedTime = project.value.poseInteraction.maxDeadTime.value)

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

        client = PoseClient(project.value.poseInteraction.port.value)
        client.onPosesReceived += { rawPoses ->
            poseBuffer = rawPoses
            lastReceiveTimeStamp = System.currentTimeMillis()
        }

        trackerThread = thread(start = true) {
            isRunning.set(true)

            while (isRunning.get()) {
                val validPoses = poseBuffer.filter { it.score >= project.value.poseInteraction.minScore.value }

                // run tracking
                simpleTracker.maxDelta = project.value.poseInteraction.maxDelta.value
                simpleTracker.track(validPoses)

                // update id's and easing
                simpleTracker.entities.forEach {
                    it.item.id = it.trackingId

                    // update easing
                    it.item.easedPosition.target.set(it.item.position)
                    it.item.easedPosition.easing = project.value.poseInteraction.positionEasing.value
                    it.item.easedPosition.update()
                }

                // update poses (make this once in the loop)
                relevantPoses.set(simpleTracker.entities.map { it.item }
                        .filter { System.currentTimeMillis() - it.startTimestamp > project.value.poseInteraction.minAliveTime.value }
                        .toList())

                // update ui
                project.value.poseInteraction.poseCount.value = "${simpleTracker.entities.size}"

                // reset buffer if nothing received
                if (System.currentTimeMillis() - lastReceiveTimeStamp >= project.value.poseInteraction.maxReceiveTimeout.value) {
                    poseBuffer = emptyList()
                    lastReceiveTimeStamp = System.currentTimeMillis()
                }

                Thread.sleep(1000 / project.value.poseInteraction.trackingFPS.value)
            }
        }
    }

    fun stop() {
        if (!isRunning.get()) return

        println("pose client stopping...")
        client.server.close()

        isRunning.set(false)
        trackerThread.join(5000)
    }

    fun renderDebug(g: PGraphics) {
        if (!isRunning.get())
            return

        g.background(0f, 100f)

        // render tracked poses
        if (project.value.poseInteraction.showTrackedPoses.value) {
            val psTracked = poses
            for (pose in psTracked) {
                g.noStroke()
                g.fill(360.0f * (pose.id % 10) / 10.0f, 80f, 100f)
                pose.keypoints.forEach {
                    g.circle(it.x * g.width, it.y * g.height, 20f)
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
                g.ellipse(pose.position.x * g.width, pose.position.y * g.height, g.width * maxDelta, g.height * maxDelta)
            }
        }
    }
}