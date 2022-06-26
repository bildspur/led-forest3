package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.ColorMixer
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.ledforest.util.windowedMappedInOut
import ch.bildspur.util.map

class PulseScene(project: Project, tubes: List<Tube>) : BaseScene("Pulse", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    private val colorMixer = ColorMixer()

    override fun setup() {
        project.interaction.mappingSpace.fire()
    }

    override fun update() {
        val pulses = project.pulseScene.pulses
        project.pulseScene.pulseCount.value = "${pulses.size}"

        val currentTime = System.currentTimeMillis()
        if (pulses.isNotEmpty()) {
            tubes.forEach {
                it.leds.forEach { led -> applyToLED(led, currentTime, pulses) }
            }
        } else {
            tubes.forEachLED {
                it.color.brightness = 0.0f
            }
        }

        // cleanup
        pulses.removeIf { !it.isAlive(currentTime) }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    private fun applyToLED(led: LED, currentTime: Long, pulses: List<Pulse>) {
        val position = led.position

        colorMixer.init()

        for (pulse in pulses) {
            val distance = position.dist(pulse.location.value)
            val pulseRadius = pulse.getPulseRadius(currentTime)

            val applyDist = pulseRadius - distance

            val width = pulse.width.value
            var factor = windowedMappedInOut(
                (applyDist + (width * 0.5f)) / width,
                pulse.attackCurve.value,
                pulse.releaseCurve.value
            )

            if (factor > 0f) {
                val color = pulse.color.value.toHSV()

                // applied dist is between -0.5 and 0.5 if w = 1.0
                val hw = (width * 0.5f)

                if (pulseRadius <= hw) {
                    factor *= pulse.attackCurve.value.method(pulseRadius / hw)
                } else if (pulseRadius >= pulse.distance.value - hw) {
                    factor *= pulse.releaseCurve.value.method((pulse.distance.value - pulseRadius) / hw)
                }

                val brightness = factor.map(0f, 1f, 0f, color.v.toFloat())

                colorMixer.addColor(
                    color.h.toFloat(),
                    color.s.toFloat(),
                    brightness, factor
                )
            }
        }

        val mixedColor = colorMixer.mixedColor
        led.color.hue = mixedColor.h.toFloat()
        led.color.saturation = mixedColor.s.toFloat()
        led.color.brightness = mixedColor.v.toFloat()
    }
}