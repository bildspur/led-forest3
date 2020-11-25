package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.pose.Pose
import ch.bildspur.ledforest.pose.PoseDataProvider
import processing.core.PVector

class PoseScene(project: Project, tubes: List<Tube>, val poseProvider: PoseDataProvider)
    : BaseInteractionScene("Pose Scene", project, tubes) {

    private val task = TimerTask(0, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction }.toList()
    }

    override fun update() {
        if (!poseProvider.isRunning.get())
            return

        val poses = poseProvider.poses

        // interaction tubes
        tubes.forEach {
            it.leds.forEachIndexed { i, led -> interactWithLED(i, led, it, poses) }
        }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    override val isInteracting: Boolean
        get() = poseProvider.poseCount != 0

    private fun interactWithLED(index: Int, led: LED, tube: Tube, poses: List<Pose>) {
        val config = project.poseInteraction
        val ledPosition = getLEDPosition(index, tube)

        // sum light by poses
        var hue = 0f
        var saturation = 0f
        var brightness = 0f

        var relevantPoseCount = 0

        for (pose in poses) {
            // get distance to led
            val posePosition = pose.position.mapPose()
            val distance = posePosition.dist(ledPosition)

            // check if is relevant for interaction
            if (distance > config.interactionDistance.value) continue

            // todo: add mapping for values (more easing curves)
            hue += 100f
            saturation += 80f
            brightness += 100f

            relevantPoseCount++
        }

        // prevent zero bug
        if (relevantPoseCount == 0)
            relevantPoseCount = 1

        led.color.fadeH(hue, 0.1f)
        led.color.fadeS(saturation / relevantPoseCount, 0.1f)
        led.color.fadeB(brightness / relevantPoseCount, 0.1f)
    }
}

fun PVector.mapPose(): PVector {
    // todo: add flipping
    // todo: think about how to set y (2d height)
    val box = Sketch.instance.project.value.interaction.interactionBox.value
    return PVector((this.x * 2.0f - 1.0f) * box.x / 2f,
            0.5f, //(this.z * 2.0f - 1.0f) * box.y / 2f,
            ((1.0f - this.y) * 2.0f - 1.0f) * box.z / 2f)
}