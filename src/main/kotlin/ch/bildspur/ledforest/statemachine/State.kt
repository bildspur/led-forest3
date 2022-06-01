package ch.bildspur.ledforest.statemachine

interface State {
    val name: String
    fun activate()
    fun update(): StateResult
    fun deactivate()
}