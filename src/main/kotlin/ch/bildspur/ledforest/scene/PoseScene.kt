package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.util.EasingCurves
import ch.bildspur.ledforest.util.limit
import ch.bildspur.ledforest.util.modValue
import processing.core.PApplet
import processing.core.PVector
import java.lang.Integer.max

class PoseScene(project: Project, tubes: List<Tube>, val poseProvider: PoseDataProvider)
    : BaseInteractionScene("Pose Scene", project, tubes) {

    private val task = TimerTask(0, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()

    data class Interactor(val position: PVector,
                          val hue: Float,
                          val saturation: Float,
                          val brightness: Float,
                          val impactRadius: Float)

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction }.toList()
    }

    override fun update() {
        if (!poseProvider.isRunning.get())
            return
        val config = project.poseInteraction

        // receive poses
        val poses = poseProvider.poses

        // create interactors
        val interactors = mutableListOf<Interactor>()
        poses.forEach {
            interactors.add(Interactor(it.easedPosition,
                    config.hueSpectrum.value.modValue(0.5f), config.saturation.value, config.brightness.value,
                    config.interactionDistanceRange.value.low.toFloat()))

            // add hand interactors
            createHandInteractor(interactors, it.leftShoulder, it.leftElbow, it.leftWrist)
            createHandInteractor(interactors, it.rightShoulder, it.rightElbow, it.rightWrist)
        }

        // interaction tubes
        iaTubes.forEach {
            it.leds.forEach { led -> interactWithLED(led, interactors) }
        }
    }

    private fun createHandInteractor(interactors: MutableList<Interactor>, shoulder: PVector, elbow: PVector, wrist: PVector) {
        // only do it if points are valid
        if (shoulder.isInvalid() || elbow.isInvalid() || wrist.isInvalid()) return
        val config = project.poseInteraction

        // calculate ratio
        val maxDistance = PVector.dist(shoulder, elbow) + PVector.dist(elbow, wrist)
        val currentDistance = PVector.dist(shoulder, wrist)
        val ratio = currentDistance / maxDistance

        // todo: calculate angles for hue range modulation
        val angle = PVector.angleBetween(PVector.sub(shoulder, elbow), PVector.sub(wrist, elbow))
        val angleDegree = PApplet.degrees(angle)
        val angleRatio = angleDegree / 180f

        interactors.add(Interactor(wrist,
                config.hueSpectrum.value.modValue(angleRatio), config.saturation.value, config.brightness.value,
                config.interactionDistanceRange.value.modValue(ratio)))
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    override val isInteracting: Boolean
        get() = poseProvider.poses.isNotEmpty()

    private fun interactWithLED(led: LED, interactors: List<Interactor>) {
        val config = project.poseInteraction
        val ledPosition = led.position

        // sum light by poses
        var hue = 0f
        var saturation = 0f
        var brightness = 0f

        var relevantPoseCount = 0

        for (interactor in interactors) {
            // get distance to led
            val posePosition = interactor.position.mapPose()
            val distance = posePosition.dist(ledPosition)

            // check if is relevant for interaction
            if (distance > interactor.impactRadius) continue

            // inversed norm delta 1.0 => very close
            val normDelta = 1f - distance / interactor.impactRadius
            hue += interactor.hue
            saturation += interactor.saturation
            brightness += interactor.brightness * EasingCurves.easeOutSine(normDelta)

            relevantPoseCount++
        }

        // prevent zero bug
        val divider = max(relevantPoseCount, 1)

        // limit hue
        val finalHue = hue.limit(config.hueSpectrum.value.low.toFloat(), config.hueSpectrum.value.high.toFloat())

        led.color.fadeH(finalHue, config.fadingSpeed.value)
        led.color.fadeS(saturation / divider, config.fadingSpeed.value)
        led.color.fadeB(brightness / divider, config.fadingSpeed.value)
    }

    private fun PVector.isInvalid(): Boolean {
        return this.x == 0f && this.y == 0f
    }
}

fun PVector.mapPose(): PVector {
    val box = Sketch.instance.project.value.interaction.interactionBox.value
    val config = Sketch.instance.project.value.poseInteraction

    val v = PVector(if (config.flipX.value) 1f - this.x else this.x,
            if (config.flipY.value) 1f - this.y else this.y,
            if (config.flipZ.value) 1f - this.z else this.z)

    // todo: think about how to set y (2d height)
    return PVector((v.x * 2.0f - 1.0f) * box.x / 2f,
            0.5f, //(v.z * 2.0f - 1.0f) * box.y / 2f,
            // height: maybe not use -1 scaling cause of bounding box
            (1.0f - v.y) * box.z / 2f)
}