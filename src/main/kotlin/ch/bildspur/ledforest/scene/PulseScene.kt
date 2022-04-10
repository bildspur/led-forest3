package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.ColorUtil
import ch.bildspur.ledforest.util.limit
import ch.bildspur.ledforest.util.windowedMappedInOut
import ch.bildspur.math.pow
import kotlin.math.sqrt

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
        pulses.removeIf { it.getPulseRadius(currentTime) > project.interaction.interactionBox.value.mag() }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    private fun applyToLED(index: Int, led: LED, tube: Tube, currentTime: Long, pulses: List<Pulse>) {
        val position = Sketch.instance.spaceInformation.getLEDPosition(index, tube)

        val huesAndWeights = mutableListOf<ColorUtil.HueAndWeight>()
        var saturation = 0f
        var brightness = 0f

        var applyCount = 0

        for (pulse in pulses) {
            val distance = position.dist(pulse.location.value)
            val pulseRadius = pulse.getPulseRadius(currentTime)

            val applyDist = pulseRadius - distance

            val width = pulse.width.value
            val factor = windowedMappedInOut((applyDist + (width * 0.5f)) / width, pulse.attackCurve.value, pulse.releaseCurve.value)

            brightness += factor

            if(factor > 0f) {
                huesAndWeights.add(ColorUtil.HueAndWeight(pulse.hue.value, factor))
                saturation += pow(pulse.saturation.value, 2f)
                applyCount++
            }
        }

        led.color.hue = ColorUtil.mixHueWeighted(huesAndWeights)
        led.color.saturation = sqrt(saturation / applyCount)
        led.color.brightness = brightness.limit(0f, 1f) * 100f
    }
}