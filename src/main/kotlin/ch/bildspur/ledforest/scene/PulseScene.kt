package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.limit
import ch.bildspur.ledforest.util.windowedSine
import ch.bildspur.ledforest.util.windowedSineIn
import ch.bildspur.ledforest.util.windowedSineOut
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
        pulses.removeIf { it.getPulseRadius(currentTime).magSq() > project.interaction.interactionBox.value.magSq() * 4 }
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

            val applyDist = PVector(
                    kotlin.math.abs(distance - pulseRadius.x),
                    kotlin.math.abs(distance - pulseRadius.y),
                    kotlin.math.abs(distance - pulseRadius.z)
            )

            val factors = PVector(
                    windowedSineOut(applyDist.x / pulse.width.x),
                    windowedSineOut(applyDist.y / pulse.width.y),
                    windowedSineOut(applyDist.z / pulse.width.z)
            )

            // debug info
            if(index == 10 && tube.universe.value == 4 && tube.addressStart.value == 0) {
                Sketch.instance.peasy.hud {
                    Sketch.instance.text("ApplyD: ${applyDist.x}", 20f, 20f)
                    Sketch.instance.text("Factor: ${factors.x}", 20f, 50f)
                }
            }

            brightness += factors.x // (factors.x + factors.y + factors.z) / 3f
        }

        led.color.brightness = brightness.limit(0f, 1f) * 100f
    }
}