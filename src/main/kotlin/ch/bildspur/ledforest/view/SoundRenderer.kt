package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ddf.minim.AudioPlayer
import ddf.minim.Minim

class SoundRenderer(val project: Project, val minim: Minim, val leap: LeapDataProvider, val tubes: List<Tube>) : IRenderer {
    override val timerTask: TimerTask
        get() = TimerTask(0, { render() })

    lateinit var backgroundPlayer: AudioPlayer

    override fun setup() {
        println("!=!=!= adding audio renderer...")

        backgroundPlayer = minim.loadFile("sound/background.wav", 2048)
        backgroundPlayer.loop()
    }

    override fun render() {

    }

    override fun dispose() {
        println("!=!=!=  disposing audio renderer...")

        backgroundPlayer.pause()
        backgroundPlayer.close()
    }
}