package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube

class PassthroughScene(project: Project, tubes: List<Tube>) : BaseScene("Passthrough", project, tubes) {
    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
    }

    override fun update() {
    }

    override fun stop() {
    }

    override fun dispose() {
    }
}