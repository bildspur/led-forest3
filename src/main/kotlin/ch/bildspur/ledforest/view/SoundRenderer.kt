package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.sound.EasingAudioPlayer
import ch.bildspur.ledforest.util.limit
import ddf.minim.Minim
import processing.core.PApplet

class SoundRenderer(val project: Project, val minim: Minim, val leap: LeapDataProvider, val tubes: List<Tube>) : IRenderer {
    private val task = TimerTask(0, { render() }, "SoundRender")
    override val timerTask: TimerTask
        get() = task

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
        // update players
        handPlayer.update()
        backgroundPlayer.update()

        // check for hands
        if (!leap.isRunning)
            return

        val hands = leap.hands
        try {
            if (hands.isEmpty()) {
                handPlayer.volume.target = EasingAudioPlayer.MUTED_GAIN
            } else {
                handPlayer.volume.target = EasingAudioPlayer.DEFAULT_GAIN
                val average = (hands.sumByDouble { it.position.x.toDouble() } / hands.size.toDouble()).toFloat()
                handPlayer.player.pan = PApplet.map(average, 0f, project.interactionBox.value.x, 0f, 1f).limit(-1f, 1f)
            }
        } catch (ex: Exception) {
            println("LCB 2: ${ex.message}")
        }
    }

    override fun dispose() {
        handPlayer.player.pause()
        handPlayer.player.close()

        backgroundPlayer.player.pause()
        backgroundPlayer.player.close()
    }
}