package ch.bildspur.ledforest.statemachine

class StateMachine(private val initialState: State) {
    private var activeState = initialState

    fun setup() {
        activeState = initialState
        activeState.activate()
    }

    fun update() {
        val result = activeState.update()

        if (result.nextState == null) return

        // switch states
        activeState.deactivate()
        activeState = result.nextState
        activeState.activate()
    }

    fun release() {
        activeState.deactivate()
    }

    val activeStateName: String
        get() = activeState.name
}