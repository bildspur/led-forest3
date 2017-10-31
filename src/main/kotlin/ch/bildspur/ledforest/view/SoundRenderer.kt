package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.sound.EasingAudioPlayer
import ddf.minim.Minim

class SoundRenderer(val project: Project, val minim: Minim, val leap: LeapDataProvider, val tubes: List<Tube>) : IRenderer {
    override val timerTask: TimerTask
        get() = TimerTask(0, { render() })

    lateinit var backgroundPlayer: EasingAudioPlayer
    lateinit var handPlayer: EasingAudioPlayer

    override fun setup() {
        backgroundPlayer = EasingAudioPlayer(minim.loadFile("sound/background.wav", 2048),
                easing = 0.01f,
                value = EasingAudioPlayer.MUTED_GAIN,
                target = EasingAudioPlayer.DEFAULT_GAIN)
        backgroundPlayer.player.loop()

        handPlayer = EasingAudioPlayer(minim.loadFile("sound/hand.wav", 2048),
                easing = 0.1f,
                value = EasingAudioPlayer.MUTED_GAIN,
                target = EasingAudioPlayer.MUTED_GAIN)
        handPlayer.player.loop()
    }

    override fun render() {
        // check for hands
        

        handPlayer.update()
        backgroundPlayer.update()
    }

    override fun dispose() {
        handPlayer.player.pause()
        handPlayer.player.close()

        backgroundPlayer.player.pause()
        backgroundPlayer.player.close()
    }
}