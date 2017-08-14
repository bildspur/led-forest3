package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask

interface IRenderer {
    val timerTask: TimerTask

    fun setup()

    fun render()

    fun dispose()
}