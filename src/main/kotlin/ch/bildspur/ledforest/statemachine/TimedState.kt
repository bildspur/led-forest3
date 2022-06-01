package ch.bildspur.ledforest.statemachine

import ch.bildspur.timer.ElapsedTimer

class TimedState(
    name: String,
    duration: Long,
    var nextState: State? = null,
    onActivate: () -> Unit = {},
    onUpdate: () -> StateResult = { StateResult() },
    onDeactivate: () -> Unit = {}
) : CustomState(name, onActivate, onUpdate, onDeactivate) {
    val timer = ElapsedTimer(duration)


    override fun activate() {
        timer.reset()
        super.activate()
    }

    override fun update(): StateResult {
        val result = onUpdate()

        if (result.nextState == null && timer.elapsed()) {
            return StateResult(nextState)
        }

        return result
    }
}