package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.colorizeEach
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.util.map


class StarPatternScene(project: Project, tubes: List<Tube>) : BaseScene("StarPattern", project, tubes) {
    private val task = TimerTask(project.starPattern.timerInterval.value, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()
    var cubeTubes = emptyList<Tube>()

    init {
        // allow on the fly change!
        project.starPattern.color.onChanged += {
            if (project.starPattern.overwriteColor.value) {
                println("overwriting color")
                tubes.forEachLED {
                    val hsv = project.starPattern.color.value.toHSV()
                    it.color.fadeH(hsv.h.toFloat(), 0.1f)
                    it.color.fadeS(hsv.s.toFloat(), 0.1f)
                }
            }
        }

        project.starPattern.timerInterval.onChanged += {
            task.interval = it
        }
    }

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction }.toList()
        cubeTubes = tubes.filter { it.tag.value == TubeTag.CubeBottom || it.tag.value == TubeTag.CubeTop }.toList()

        tubes.forEachLED {
            if (project.starPattern.overwriteColor.value) {
                val hsv = project.starPattern.color.value.toHSV()
                it.color.fadeH(hsv.h.toFloat(), 0.05f)
                it.color.fadeS(hsv.s.toFloat(), 0.05f)
            }

            it.color.fadeB(mapToBrightness(0f), 0.05f)
        }

        // turn on cube leds
        cubeTubes.forEach { tube ->
            tube.leds.forEach {
                it.color.fadeH(300f, 0.1f)
                it.color.fadeS(100f, 0.05f)
                it.color.fadeB(50f, 0.05f)
            }
        }
    }

    override fun update() {
        val config = project.starPattern

        iaTubes.colorizeEach(perElement = project.starPattern.applyPerElement.value) {
            val ledBrightness = ColorMode.brightness(it.color.color)

            if (ledBrightness > mapToBrightness(10f)) {
                //led is ON
                if (Sketch.instance.random(0f, 1f) > config.randomOffFactor.value) {
                    it.color.fadeB(mapToBrightness(0f), config.fadeSpeed.value)
                }
            } else {
                //led is OFF
                if (Sketch.instance.random(0f, 1f) > config.randomOnFactor.value) {
                    it.color.fadeB(
                        Sketch.instance.random(mapToBrightness(50f), mapToBrightness(100f)),
                        config.fadeSpeed.value
                    )
                }
            }
        }
    }

    private fun mapToBrightness(value: Float): Float {
        val brightnessSpectrum = project.starPattern.brightnessSpectrum.value
        return value.map(0f, 100f, brightnessSpectrum.low.toFloat(), brightnessSpectrum.high.toFloat())
    }

    override fun stop() {

    }

    override fun dispose() {

    }
}