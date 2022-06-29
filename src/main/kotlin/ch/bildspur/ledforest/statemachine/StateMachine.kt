package ch.bildspur.ledforest.statemachine

import ch.bildspur.event.Event

class StateMachine(private val initialState: State) {
    private var activeState = initialState

    val onStateChanged = Event<State>()

    fun setup() {
        activeState = initialState
        activeState.activate()
    }

    fun update() {
        val result = activeState.update()

        if (result.nextState == null) return

        // switch states
        switch(result.nextState)
    }

    fun switch(nextState: State) {
        activeState.deactivate()
        activeState = nextState
        onStateChanged(activeState)
        activeState.activate()
    }

    fun release() {
        activeState.deactivate()
    }

    val activeStateName: String
        get() = activeState.name
}