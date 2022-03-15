package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.limit
import ch.bildspur.ledforest.util.windowedSine
import processing.core.PVector
import java.lang.Math.abs

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
        pulses.removeIf { it.getPulseRadius(currentTime).magSq() > project.interaction.interactionBox.value.magSq() }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    private fun applyToLED(index: Int, led: LED, tube: Tube, currentTime: Long, pulses: List<Pulse>) {
        val position = Sketch.instance.spaceInformation.getLEDPosition(index, tube)

        var brightness = 0f

        for(pulse in pulses) {
            val distance = position.dist(pulse.location)
            val pulseRadius = pulse.getPulseRadius(currentTime)
            val pulseDistance = pulseRadius.dist(pulse.location)

            val applyDist = (distance - pulseDistance)

            /*
            val applyDist = PVector(
                    (kotlin.math.abs(position.x - pulse.location.x)) - pulseRadius.x,
                    (kotlin.math.abs(position.y - pulse.location.y)) - pulseRadius.y,
                    (kotlin.math.abs(position.z - pulse.location.z)) - pulseRadius.z
            )
             */

            val factors = PVector(
                    windowedSine(applyDist / pulse.width.x),
                    windowedSine(applyDist / pulse.width.y),
                    windowedSine(applyDist / pulse.width.z)
            )

            brightness += factors.x // (factors.x + factors.y + factors.z) / 3f
        }

        led.color.setB(brightness.limit(0f, 1f) * 100f)
    }
}