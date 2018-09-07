package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.forEachLED


class StarPatternScene(project: Project, tubes: List<Tube>) : BaseScene("StarPattern Scene", project, tubes) {
    private var randomOnFactor = 0.95f
    private var randomOffFactor = 0.8f
    private var fadeSpeed = 0.01f

    private val task = TimerTask(500, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()
    var cubeTubes = emptyList<Tube>()

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction }.toList()
        cubeTubes = tubes.filter { it.tag.value == TubeTag.CubeBottom || it.tag.value == TubeTag.CubeTop }.toList()

        tubes.forEachLED {
            it.color.fadeB(0f, 0.05f)
        }

        // turn on cube leds
        cubeTubes.forEach {
            it.leds.forEach {
                it.color.fadeH(0f, 0.1f)
                it.color.fadeS(0f, 0.05f)
                it.color.fadeB(100f, 0.05f)
            }
        }
    }

    override fun update() {
        iaTubes.forEachLED {
            val ledBrightness = ColorMode.brightness(it.color.color)

            if (ledBrightness > 10) {
                //led is ON
                if (Sketch.instance.random(0f, 1f) > randomOffFactor) {
                    it.color.fadeB(0f, fadeSpeed)
                }
            } else {
                //led is OFF
                if (Sketch.instance.random(0f, 1f) > randomOnFactor) {
                    it.color.fadeB(Sketch.instance.random(50f, 100f), fadeSpeed)
                }
            }
        }
    }

    override fun stop() {

    }

    override fun dispose() {

    }
}