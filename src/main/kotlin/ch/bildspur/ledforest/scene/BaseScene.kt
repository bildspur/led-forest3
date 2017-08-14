package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.light.Tube

abstract class BaseScene(val tubes: List<Tube>) {
    abstract val name: String

    abstract val timerTask: TimerTask

    abstract fun setup()
    abstract fun update()
    abstract fun stop()
    abstract fun dispose()
}