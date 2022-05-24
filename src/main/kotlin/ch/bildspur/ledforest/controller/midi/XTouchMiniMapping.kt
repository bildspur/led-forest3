package ch.bildspur.ledforest.controller.midi

import ch.bildspur.color.HSV
import ch.bildspur.ledforest.model.Project
import themidibus.MidiBus

class XTouchMiniMapping : MidiMapping("X-TOUCH MINI") {
    override fun attach(project: Project, midi: MidiBus) {

    }

    override fun detach(project: Project) {

    }

    override fun noteOn(project: Project, midi: MidiBus, channel: Int, pitch: Int, velocity: Int) {
        midi.sendNoteOn(10, 22, 0)
        midi.sendNoteOn(10, 21, 0)

        if (channel == 10 && pitch == 22) {
            // interaction
            project.pulseScene.enabled.value = true
            project.leda.enabled.value = true

            project.cloudScene.enabled.value = false
            midi.sendNoteOn(10, 22, 127)
        }

        if (channel == 10 && pitch == 21) {
            // idle
            project.pulseScene.enabled.value = false
            project.leda.enabled.value = false

            project.cloudScene.enabled.value = true
            midi.sendNoteOn(10, 21, 127)
        }
    }

    override fun noteOff(project: Project, midi: MidiBus, channel: Int, pitch: Int, velocity: Int) {

    }

    override fun controllerChange(project: Project, midi: MidiBus, channel: Int, number: Int, value: Int) {
        if (channel == 10 && number == 9) {
            project.light.luminosity.value = value / 127f
        } else if (channel == 10 && number == 8) {
            project.starPattern.color.value = HSV((value / 127f * 360).toInt(), 100, 100).toRGB()
        }
    }

}