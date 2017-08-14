package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.light.Tube

class LeapMotionScene(tubes: List<Tube>) : BaseScene(tubes) {

    override val name: String
        get() = "LeapMotion Scene"

    override val timerTask: TimerTask
        get() = TimerTask(0, { update() })

    override fun setup() {

    }

    override fun update() {

    }

    override fun stop() {

    }

    override fun dispose() {

    }
}