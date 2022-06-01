package ch.bildspur.ledforest.statemachine

interface State {
    fun activate()
    fun update(): StateResult
    fun deactivate()
}