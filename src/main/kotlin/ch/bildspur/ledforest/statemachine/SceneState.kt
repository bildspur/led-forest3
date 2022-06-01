package ch.bildspur.ledforest.statemachine

import ch.bildspur.ledforest.scene.BaseScene

class SceneState(
    var scene: BaseScene,
    onActivate: () -> Unit = {},
    onUpdate: () -> StateResult = { StateResult() },
    onDeactivate: () -> Unit = {}
) : CustomState(scene.name, onActivate, onUpdate, onDeactivate) {

    override fun activate() {
        scene.setup()
        super.activate()
    }

    override fun update(): StateResult {
        scene.update()
        return super.update()
    }

    override fun deactivate() {
        scene.stop()
        super.deactivate()
    }
}