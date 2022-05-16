package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.LightElement
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.forEachLED

class TestScene(project: Project, tubes: List<Tube>) : BaseScene("Test Scene", project, tubes) {
    private val task = TimerTask(project.test.interval.value, { update() })

    override val timerTask: TimerTask
        get() = task

    private var elements = project.lightElements
    private var ledsByUniverse = emptyMap<Int, List<LED>>()

    private var index = 0
    private var maxLeds = 0

    override fun setup() {
        elements = project.lightElements
        ledsByUniverse = elements.sortedBy { it.startAddress }.groupBy { it.universe.value }
            .mapValues { it.value.flatMap { it.leds } }

        maxLeds = ledsByUniverse.maxOf { it.value.size }

        // set all led's one black
        elements.forEachLED {
            it.color.fade(ColorMode.color(0), 0.05f)
        }
    }

    override fun update() {
        timerTask.interval = project.test.interval.value
        index = (index + 1) % maxLeds

        ledsByUniverse.map { it.value }.forEach { leds ->
            leds.forEachIndexed { i, led ->
                if (index <= i && i < index + project.test.size.value) {
                    led.color.fade(ColorMode.color(150), 0.25f)
                } else {
                    led.color.fade(ColorMode.color(60), 0.25f)
                }
            }
        }
    }

    override fun stop() {
    }

    override fun dispose() {
    }
}