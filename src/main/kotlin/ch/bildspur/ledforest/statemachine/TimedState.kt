package ch.bildspur.ledforest.statemachine

import ch.bildspur.timer.ElapsedTimer

class TimedState(
    duration: Long,
    private val nextState: State,
    onActivate: () -> Unit = {},
    onUpdate: () -> StateResult = { StateResult() },
    onDeactivate: () -> Unit = {}
) : CustomState(onActivate, onUpdate, onDeactivate) {
    val timer = ElapsedTimer(duration)

    override fun update(): StateResult {
        val result = onUpdate()

        if (result.nextState == null && timer.elapsed()) {
            return StateResult(nextState)
        }

        return result
    }
}