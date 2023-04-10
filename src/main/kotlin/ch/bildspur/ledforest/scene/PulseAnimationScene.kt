package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube

abstract class PulseAnimationScene(name: String, val pulseScene: PulseScene, project: Project, tubes: List<Tube>) :
    BaseScene(name, project, tubes) {

    private val task = TimerTask(10, { update() })
    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        pulseScene.setup()
    }

    override fun update() {
        if (!project.leda.enableRandomPulses.value) {
            pulseScene.update()
            return
        }

        animatePulses()
        pulseScene.update()
    }

    override fun stop() {
        pulseScene.stop()
    }

    override fun dispose() {
        pulseScene.dispose()
    }

    abstract fun animatePulses()
}