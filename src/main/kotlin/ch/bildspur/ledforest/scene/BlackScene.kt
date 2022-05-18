package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.forEachLED

class BlackScene(project: Project, tubes: List<Tube>) : BaseScene("Black Scene", project, tubes) {
    private val task = TimerTask(1000, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        // set all led's one black
        project.lightElements.forEachLED {
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