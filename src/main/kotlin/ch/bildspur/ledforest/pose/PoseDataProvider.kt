package ch.bildspur.ledforest.pose

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.util.toFloat2
import ch.bildspur.model.DataModel
import ch.bildspur.simple.SimpleTracker
import processing.core.PApplet
import processing.core.PGraphics
import javax.swing.Spring.height




/**
 * Does the pose tracking!
 */
class PoseDataProvider(val sketch: PApplet, val project: DataModel<Project>) {
    lateinit var client: PoseClient

    var isRunning = false

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

    fun start() {
        if (isRunning) return

        println("pose client starting up...")

        client = PoseClient(project.value.poseInteraction.port.value)
        client.onPosesReceived += { poses ->
            simpleTracker.maxDelta = project.value.poseInteraction.maxDelta.value
            simpleTracker.track(poses.filter { it.score > project.value.poseInteraction.minScore.value })
            project.value.poseInteraction.poseCount.value = "${simpleTracker.entities.size}"
        }
        isRunning = true
    }

    fun stop() {
        if (!isRunning) return

        println("pose client stopping...")

        client.osc.stop()
        isRunning = false
    }

    fun renderDebug(g: PGraphics) {
        if (!isRunning)
            return

        g.background(0f, 100f)
        for (pose in poses) {
            g.fill(360.0f * (pose.id.toFloat() / client.poses.size), 80f, 100f)
            pose.keypoints.forEach {
                g.noStroke()
                g.circle(it.x * g.width, it.y * g.height, 20f)
            }
        }
    }
}