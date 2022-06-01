package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.Sketch
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
                target = project.audio.backgroundGain.value.toFloat())
        backgroundPlayer.player.loop()

        handPlayer = EasingAudioPlayer(minim.loadFile("sound/hand.wav", 2048),
                easing = 0.1f,
                value = EasingAudioPlayer.MUTED_GAIN,
                target = EasingAudioPlayer.MUTED_GAIN)
        handPlayer.player.loop()
    }

    override fun render() {
        // set loudness
        backgroundPlayer.volume.target = project.audio.backgroundGain.value.toFloat()

        // update players
        handPlayer.update()
        backgroundPlayer.update()

        val rs = Sketch.instance.realSense

        // check for hands
        if (!leap.isRunning && !rs.isRunning)
            return

        val hands = leap.hands

        // stop sound
        if (hands.isEmpty() && rs.activeRegions.isEmpty()) {
            handPlayer.volume.target = EasingAudioPlayer.MUTED_GAIN
        }

        // enable if leap is there
        if (!hands.isEmpty()) {
            handPlayer.volume.target = project.audio.rattleGain.value.toFloat()
            val average = (hands.sumOf { it.position.x.toDouble() } / hands.size.toDouble()).toFloat()
            handPlayer.player.pan = PApplet.map(average, 0f, project.interaction.mappingSpace.value.x, 0f, 1f).limit(-1f, 1f)
        }

        // enable if realsense is there
        if (!rs.activeRegions.isEmpty()) {
            handPlayer.volume.target = project.audio.rattleGain.value.toFloat()
            val average = rs.activeRegions.map { it.normalizedPosition.x }.average().toFloat()
            handPlayer.player.pan = PApplet.map(average, 0f, project.interaction.mappingSpace.value.x, 0f, 1f).limit(-1f, 1f)
        }
    }

    override fun dispose() {
        handPlayer.player.pause()
        handPlayer.player.close()

        backgroundPlayer.player.pause()
        backgroundPlayer.player.close()
    }
}