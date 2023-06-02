package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.forEachLED

class LedaShowScene(project: Project, tubes: List<Tube>) : BaseScene("Leda Show", project, tubes) {
    private val task = TimerTask(1, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        // reset show trigger flags
        project.leda.ledaShow.showTrigger.value = false
        project.leda.ledaShow.hasShowEnded.value = false

        // todo: find relevant video scene and play it back
    }

    override fun update() {
        tubes.forEachLED { it.color.set(ColorMode.color(200, 100, 100)) }
    }

    override fun stop() {
    }

    override fun dispose() {
    }


}