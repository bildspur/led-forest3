package ch.bildspur.ledforest.pose

import ch.bildspur.ledforest.model.Project
import ch.bildspur.model.DataModel
import processing.core.PApplet

class PoseDataProvider(val sketch: PApplet, val project: DataModel<Project>) {
    lateinit var client : PoseClient

    var isRunning = false

    val poses: MutableList<Pose>
        get() = client.poses

    fun start() {
        if(isRunning) return

        client = PoseClient(project.value.poseInteraction.port.value)
        isRunning = true
    }

    fun stop() {
        if(!isRunning) return

        client.osc.stop()
        isRunning = false
    }
}