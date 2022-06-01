package ch.bildspur.ledforest.statemachine

open class CustomState(
    var onActivate: () -> Unit = {},
    var onUpdate: () -> StateResult = { StateResult() },
    var onDeactivate: () -> Unit = {}
) : State {
    override fun activate() {
        onActivate()
    }

    override fun update(): StateResult {
        return onUpdate()
    }

    override fun deactivate() {
        onDeactivate()
    }
}