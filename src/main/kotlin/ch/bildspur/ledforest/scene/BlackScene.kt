package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.forEachLED

class BlackScene(tubes: List<Tube>) : BaseScene(tubes) {
    private val task = TimerTask(1000, { update() })

    override val name: String
        get() = "Black Scene"

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        // set all led's one black
        tubes.forEachLED {
            it.color.fade(ColorMode.color(0), 0.05f)
        }
    }

    override fun update() {
    }

    override fun stop() {
    }

    override fun dispose() {
    }
}