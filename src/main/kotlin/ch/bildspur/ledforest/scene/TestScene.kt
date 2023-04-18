package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.util.map

class TestScene(project: Project, tubes: List<Tube>) : BaseScene("Test", project, tubes) {
    private val task = TimerTask(0, { update() })

    override val timerTask: TimerTask
        get() = task

    private var elements = project.lightElements
    private var ledsByUniverse = emptyMap<Int, List<LED>>()

    private var index = 0.0
    private var maxLeds = 0

    override fun setup() {
        elements = project.lightElements
        ledsByUniverse = elements.sortedBy { it.startAddress }.groupBy { it.universe.value }
            .mapValues { it.value.flatMap { it.leds } }

        maxLeds = ledsByUniverse.maxOf { it.value.size } + project.test.size.value

        // set all led's one black
        elements.forEachLED {
            it.color.fade(ColorMode.color(0), 0.05f)
        }
    }

    override fun update() {
        if (project.test.soloMode.value) {
            val color = project.test.color.value.toHSV()

            project.tubes.forEach { t ->
                t.leds.forEachIndexed { index, led ->
                    if (t.isSelected.value) {
                        var hue = color.h
                        var saturation = color.s

                        if (project.test.colorDirection.value) {
                            hue = index.map(0, t.leds.size,  0, 250)
                            saturation = 100
                        }

                        led.color.fade(ColorMode.color(hue, saturation, 100), project.test.fade.value)
                    } else {
                        led.color.fade(ColorMode.color(color.h, color.s, 0), project.test.fade.value)
                    }
                }
            }
            return
        }

        index = (index + project.test.speed.value)
        if (index >= maxLeds)
            index = -project.test.size.value.toDouble()

        val color = project.test.color.value.toHSV()

        ledsByUniverse.map { it.value }.forEach { leds ->
            leds.forEachIndexed { i, led ->
                if (index <= i && i < index + project.test.size.value) {
                    led.color.fade(ColorMode.color(color.h, color.s, 100), project.test.fade.value)
                } else {
                    led.color.fade(ColorMode.color(color.h, color.s, 0), project.test.fade.value)
                }
            }
        }
    }

    override fun stop() {
    }

    override fun dispose() {
    }
}