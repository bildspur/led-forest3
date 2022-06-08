package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.util.*
import ch.bildspur.math.mix
import processing.core.PApplet
import processing.core.PVector
import java.lang.Integer.max
import kotlin.math.sqrt

class PoseScene(project: Project, tubes: List<Tube>, val poseProvider: PoseDataProvider) :
        BaseInteractionScene("Pose", project, tubes) {

    private val task = TimerTask(0, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()

    private val colorMixer = ColorMixer()

    data class Reactor(
            val position: PVector,
            val hue: Float,
            val saturation: Float,
            val brightness: Float,
            val impactRadius: Float
    )

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction }.toList()
    }

    override fun update() {
        if (!poseProvider.isRunning.get())
            return
        val config = project.poseInteraction

        // receive poses
        val poses = poseProvider.poses

        // create reactors
        val reactors = config.activeReactors
        reactors.clear()
        poses.forEach {
            // add hand reactors
            createHandReactor(reactors, it.leftShoulder, it.leftElbow, it.leftWrist,  config.hueSpectrum.value.high.toFloat())
            createHandReactor(reactors, it.rightShoulder, it.rightElbow, it.rightWrist,  config.hueSpectrum.value.low.toFloat())
        }

        // interaction tubes
        iaTubes.forEach {
            it.leds.forEach { led -> interactWithLED(led, reactors) }
        }
    }

    private fun createHandReactor(
            reactors: MutableList<Reactor>,
            shoulder: PVector,
            elbow: PVector,
            wrist: PVector,
            reactorHue: Float
    ) {
        // only do it if points are valid
        if (wrist.isInvalid()) return
        val config = project.poseInteraction

        // todo: move mapping into data-provider
        val mappedPosition = project.interaction.fromInteractionToMappingSpace(
                PVector.sub(wrist, project.leda.triggerOrigin.value)
        )

        if (project.poseInteraction.zeroZ.value) {
            mappedPosition.z = 0f
        }

        reactors.add(
                Reactor(
                        mappedPosition,
                        reactorHue, config.saturation.value, config.brightness.value,
                        config.interactionDistanceRange.value.low.toFloat()
                )
        )
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    override val isInteracting: Boolean
        get() = poseProvider.poses.isNotEmpty()

    private fun interactWithLED(led: LED, reactors: List<Reactor>) {
        val config = project.poseInteraction
        val ledPosition = led.position

        // sum light by poses
        colorMixer.init()

        for (reactor in reactors) {
            // get distance to led
            val posePosition = reactor.position
            val distance = posePosition.dist(ledPosition)

            // check if is relevant for interaction
            if (distance > reactor.impactRadius) continue

            // inversed norm delta 1.0 => very close
            val normDelta = 1f - distance / reactor.impactRadius
            val factor = EasingCurves.easeOutSine(normDelta)
            colorMixer.addColor(reactor.hue, reactor.saturation, reactor.brightness * factor, factor)
        }

        val mixedColor = colorMixer.mixedColor
        led.color.hue = mixedColor.h.toFloat()
        led.color.saturation = mixedColor.s.toFloat()
        led.color.brightness = mixedColor.v.toFloat()

        /*
        led.color.fadeH(finalHue, config.fadingSpeed.value)
        led.color.fadeS(saturation / divider, config.fadingSpeed.value)
        led.color.fadeB(brightness / divider, config.fadingSpeed.value)
         */
    }

    private fun PVector.isInvalid(): Boolean {
        return this.x == 0f && this.y == 0f
    }
}