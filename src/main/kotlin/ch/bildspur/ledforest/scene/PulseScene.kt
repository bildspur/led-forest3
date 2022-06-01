package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.ColorUtil
import ch.bildspur.ledforest.util.limit
import ch.bildspur.ledforest.util.windowedMappedInOut
import ch.bildspur.math.pow
import ch.bildspur.util.map
import kotlin.math.sqrt

class PulseScene(project: Project, tubes: List<Tube>) : BaseScene("Pulse", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        project.interaction.mappingSpace.fire()
    }

    override fun update() {
        val pulses = project.pulseScene.pulses
        project.pulseScene.pulseCount.value = "${pulses.size}"

        val currentTime = System.currentTimeMillis()
        tubes.forEach {
            it.leds.forEach { led -> applyToLED(led, currentTime, pulses) }
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

        val huesAndWeights = mutableListOf<ColorUtil.HueAndWeight>()
        var saturation = 0f
        var brightness = 0f

        var applyCount = 0

        for (pulse in pulses) {
            val distance = position.dist(pulse.location.value)
            val pulseRadius = pulse.getPulseRadius(currentTime)

            val applyDist = pulseRadius - distance

            val width = pulse.width.value
            val factor = windowedMappedInOut(
                (applyDist + (width * 0.5f)) / width,
                pulse.attackCurve.value,
                pulse.releaseCurve.value
            )

            if (factor > 0f) {
                val color = pulse.color.value.toHSV()

                brightness += factor.map(0f, 1f, 0f, color.v / 100f)
                huesAndWeights.add(ColorUtil.HueAndWeight(color.h.toFloat(), factor))
                saturation += pow(color.s.toFloat(), 2f)
                applyCount++
            }
        }

        led.color.hue = ColorUtil.mixHueWeighted(huesAndWeights)
        led.color.saturation = sqrt(saturation / applyCount)
        led.color.brightness = brightness.limit(0f, 1f) * 100f
    }
}