package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube

class SoundRenderer(val project: Project, val leap: LeapDataProvider, val tubes: List<Tube>) : IRenderer {
    override val timerTask: TimerTask
        get() = TimerTask(0, { render() })

    override fun setup() {

    }

    override fun render() {

    }

    override fun dispose() {

    }
}