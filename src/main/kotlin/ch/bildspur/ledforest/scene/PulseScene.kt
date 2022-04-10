package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.limit
import ch.bildspur.ledforest.util.windowedInOut
import ch.bildspur.util.Easing
import processing.core.PVector

class PulseScene(project: Project, tubes: List<Tube>) : BaseScene("Pulse Scene", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {

    }

    override fun update() {
        val pulses = project.pulseScene.pulses
        project.pulseScene.pulseCount.value = "${pulses.size}"

        val currentTime = System.currentTimeMillis()
        tubes.forEach {
            it.leds.forEachIndexed { i, led -> applyToLED(i, led, it, currentTime, pulses) }
        }

        // cleanup
        pulses.removeIf { it.getPulseRadius(currentTime).magSq() > project.interaction.interactionBox.value.magSq() * 4 }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    private fun applyToLED(index: Int, led: LED, tube: Tube, currentTime: Long, pulses: List<Pulse>) {
        val position = Sketch.instance.spaceInformation.getLEDPosition(index, tube)

        var brightness = 0f

        for (pulse in pulses) {
            val distance = position.dist(pulse.location)
            val pulseRadius = pulse.getPulseRadius(currentTime)

            val applyDist = PVector(
                    pulseRadius.x - distance,
                    pulseRadius.y - distance,
                    pulseRadius.z - distance
            )

            val factors = PVector(
                    (applyDist.x + (pulse.width.x * 0.5f)) / pulse.width.x,
                    (applyDist.y + (pulse.width.y * 0.5f)) / pulse.width.y,
                    (applyDist.z + (pulse.width.z * 0.5f)) / pulse.width.z
            )

            brightness += Easing.EaseOutCubic(windowedInOut(factors.x))
        }

        led.color.brightness = brightness.limit(0f, 1f) * 100f
    }
}