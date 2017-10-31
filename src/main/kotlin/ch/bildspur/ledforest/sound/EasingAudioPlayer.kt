package ch.bildspur.ledforest.sound

import ch.bildspur.ledforest.model.easing.EasingFloat
import ch.bildspur.ledforest.model.easing.EasingObject
import ddf.minim.AudioPlayer
import ddf.minim.spi.AudioOut
import ddf.minim.spi.AudioRecordingStream

class EasingAudioPlayer(recording: AudioRecordingStream?, out: AudioOut?) : AudioPlayer(recording, out), EasingObject {
    val volume = EasingFloat(1.0f)
    
    override fun update() {
        volume.update()
        this.gain = volume.value
    }
}