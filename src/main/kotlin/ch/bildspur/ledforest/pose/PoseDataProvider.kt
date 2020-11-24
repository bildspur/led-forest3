package ch.bildspur.ledforest.pose

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.util.toFloat2
import ch.bildspur.model.DataModel
import ch.bildspur.simple.SimpleTracker
import processing.core.PApplet
import processing.core.PGraphics
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


/**
 * Does the pose tracking!
 */
class PoseDataProvider(val sketch: PApplet, val project: DataModel<Project>) {
    lateinit var client: PoseClient
    lateinit var trackerThread: Thread

    var isRunning = AtomicBoolean()

    private val simpleTracker = SimpleTracker<Pose>({ it.neck.toFloat2() },
            maxDelta = project.value.poseInteraction.maxDelta.value)

    val poses: List<Pose>
        get() = simpleTracker.entities.map {
            // set specific tracking id
            it.item.id = it.trackingId
            it.item
        }.toList()

    val poseCount: Int
        get() = simpleTracker.entities.size

    var poseBuffer = emptyList<Pose>()

    fun start() {
        if (isRunning.get()) return

        println("pose client starting up...")

        client = PoseClient(project.value.poseInteraction.port.value)
        client.onPosesReceived += { rawPoses ->
            poseBuffer = rawPoses
        }

        trackerThread = thread(start = true) {
            isRunning.set(true)

            while(isRunning.get()) {
                val validPoses = poseBuffer.filter { it.score >= project.value.poseInteraction.minScore.value }

                // run tracking
                simpleTracker.maxDelta = project.value.poseInteraction.maxDelta.value
                simpleTracker.track(validPoses)

                // update ui
                project.value.poseInteraction.poseCount.value = "${simpleTracker.entities.size}"

                Thread.sleep(project.value.poseInteraction.trackingFPS.value)
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
        val psTracked = emptyList<Pose>()
        for (pose in psTracked) {
            g.noStroke()
            g.fill(360.0f * (pose.id % 10) / 10.0f, 80f, 100f)
            pose.keypoints.forEach {
                g.circle(it.x * g.width, it.y * g.height, 20f)
            }


        }

        // render detected poses
        val validPoses = poseBuffer.filter { it.score >= project.value.poseInteraction.minScore.value }
        for (pose in validPoses) {
            g.noFill()
            g.stroke(360.0f * pose.id / validPoses.size, 80f, 100f)
            pose.keypoints.forEach {
                g.circle(it.x * g.width, it.y * g.height, 25f)
            }
        }
    }
}