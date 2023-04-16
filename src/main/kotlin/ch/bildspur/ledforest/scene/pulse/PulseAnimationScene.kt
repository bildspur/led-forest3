package ch.bildspur.ledforest.scene.pulse

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.scene.BaseScene

abstract class PulseAnimationScene(
    name: String, val pulseScene: PulseScene,
    project: Project, tubes: List<Tube>,
    timerInterval: Long = 10
) :
    BaseScene(name, project, tubes) {

    private val task = TimerTask(timerInterval, { update() })
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