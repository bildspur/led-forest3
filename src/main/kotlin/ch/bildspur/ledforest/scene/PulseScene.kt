package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.windowedSine
import processing.core.PVector
import java.util.concurrent.CopyOnWriteArrayList

class PulseScene(project: Project, tubes: List<Tube>) : BaseScene("Pulse Scene", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {

    }

    override fun update() {
        // todo: cleanup waves
        val pulses = project.pulseScene.pulses

        val currentTime = System.currentTimeMillis()

        tubes.forEach {
            it.leds.forEachIndexed { i, led -> applyToLED(i, led, it, currentTime, pulses) }
        }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    private fun applyToLED(index: Int, led: LED, tube: Tube, currentTime: Long, pulses: List<Pulse>) {
        val position = Sketch.instance.spaceInformation.getLEDPosition(index, tube)

        for(pulse in pulses) {
            val distance = position.dist(pulse.location)
            val pulseRadius = PVector.mult(pulse.speed, (currentTime - pulse.startTime).toFloat())
            val pulseDistance = pulseRadius.dist(pulse.location)

            val applyDist = kotlin.math.abs(distance - pulseDistance)
            val factors = PVector(
                    windowedSine(applyDist / pulse.width.x),
                    windowedSine(applyDist / pulse.width.y),
                    windowedSine(applyDist / pulse.width.z)
            )

            if(tube.name.value == "Tube A0" && index == 12) {
                println("T: ${currentTime - pulse.startTime} D: ${distance} PR: ${pulseRadius} PD: ${pulseDistance} APD: ${applyDist} Factors: ${factors}")
            }

            led.color.setB(factors.x * 100.0f)
        }
    }
}