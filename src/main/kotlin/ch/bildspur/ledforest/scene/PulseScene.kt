package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.windowedSine
import processing.core.PVector

class PulseScene(project: Project, tubes: List<Tube>) : BaseScene("Pulse Scene", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {

    }

    override fun update() {
        // todo: cleanup waves
        val pulses = project.pulseScene.pulses

        project.pulseScene.pulseCount.value = "${pulses.size}"

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
            val pulseRadius = PVector.mult(PVector.mult(pulse.speed, 0.001f), (currentTime - pulse.startTime).toFloat())
            val pulseDistance = pulseRadius.dist(pulse.location)

            val applyDist = (distance - pulseDistance)
            val factors = PVector(
                    windowedSine(applyDist / pulse.width.x),
                    windowedSine(applyDist / pulse.width.y),
                    windowedSine(applyDist / pulse.width.z)
            )

            led.color.setB(factors.x * 100.0f)
        }
    }
}