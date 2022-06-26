package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.util.*
import ch.bildspur.math.mix
import ch.bildspur.util.map
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
            // createHandReactor(reactors, it.smoothLeftWrist,  config.hueSpectrum.value.high.toFloat())

            createHandReactor(reactors, it.smoothRightWrist, config.hueSpectrum.value.low.toFloat())
        }

        // interaction tubes
        iaTubes.forEach {
            it.leds.forEach { led -> interactWithLED(led, reactors) }
        }
    }

    private fun createHandReactor(
        reactors: MutableList<Reactor>,
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
            val posePosition = reactor.position.copy()

            if (project.poseInteraction.zeroZ.value) {
                posePosition.z = 0f
            }

            val distance = posePosition.dist(ledPosition)

            // check if is relevant for interaction
            if (distance > reactor.impactRadius) continue

            // inversed norm delta 1.0 => very close
            val normDelta = 1f - distance / reactor.impactRadius
            var factor = EasingCurves.easeOutSine(normDelta)

            if (config.invertDistanceRange.value) {
                factor = 1.0f - factor
            }

            val gradientLimit = config.gradientLimit.value
            val rgb = config.gradient.color(
                reactor.position.z
                    .limit(0.0f, 1.0f)
                    .map(0f, 1f, gradientLimit.low.toFloat(), gradientLimit.high.toFloat())
            )
            val hsv = rgb.toHSV()

            colorMixer.addColor(
                hsv.h.toFloat(), hsv.s.toFloat(),
                reactor.brightness * factor, factor
            )
        }

        val mixedColor = colorMixer.mixedColor
        led.color.hue = mixedColor.h.toFloat()
        led.color.saturation = mixedColor.s.toFloat()
        led.color.brightness = mixedColor.v.toFloat()
    }

    private fun PVector.isInvalid(): Boolean {
        return this.x == 0f && this.y == 0f
    }
}