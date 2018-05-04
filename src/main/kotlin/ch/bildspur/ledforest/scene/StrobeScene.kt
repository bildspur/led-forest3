package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.ExtendedRandom
import ch.bildspur.ledforest.util.forEachLED

class StrobeScene(tubes: List<Tube>) : BaseScene(tubes) {

    private val task = TimerTask(10, { update() })

    override val name: String
        get() = "Strobe Scene"

    override val timerTask: TimerTask
        get() = task

    val rnd = ExtendedRandom()

    val onProbability = 0.4f
    val offProbability = 0.6f

    val easing = 0.5f

    override fun setup() {
        // set all leds white
        tubes.forEachLED {
            it.color.fade(ColorMode.color(0), 0.05f)
        }
    }

    override fun update() {
        tubes.forEach { t ->
            if (rnd.randomBoolean(offProbability))
                t.leds.forEach { it.color.fadeB(0f, easing) }

            if (rnd.randomBoolean(onProbability))
                t.leds.forEach { it.color.fadeB(100f, easing) }
        }
    }

    override fun stop() {
    }

    override fun dispose() {
    }
}