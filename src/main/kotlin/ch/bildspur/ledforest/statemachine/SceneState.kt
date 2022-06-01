package ch.bildspur.ledforest.statemachine

import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.scene.BaseScene

class SceneState(
    var scene: BaseScene,
    onActivate: () -> Unit = {},
    onUpdate: () -> StateResult = { StateResult() },
    onDeactivate: () -> Unit = {}
) : CustomState(scene.name, onActivate, onUpdate, onDeactivate) {
    val timer = Timer()

    init {
        timer.addTask(scene.timerTask)
    }

    override fun activate() {
        scene.setup()
        super.activate()
    }

    override fun update(): StateResult {
        timer.update()
        return super.update()
    }

    override fun deactivate() {
        scene.stop()
        super.deactivate()
    }
}