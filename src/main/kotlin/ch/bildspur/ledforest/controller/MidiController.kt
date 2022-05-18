package ch.bildspur.ledforest.controller

import ch.bildspur.color.HSV
import ch.bildspur.ledforest.model.Project
import ch.bildspur.model.DataModel
import themidibus.MidiBus

class MidiController(
    val project: DataModel<Project>,
    val deviceName: String = "X-TOUCH MINI"
) {
    private lateinit var midi: MidiBus

    init {
        checkForInput()

        project.onChanged += {

        }
    }

    fun checkForInput() {
        MidiBus.list()
        for (input in MidiBus.availableInputs()) {
            if (input == deviceName) {
                setupInput(input)
                break
            }
        }
    }

    fun setupInput(deviceName: String) {
        midi = MidiBus(this, deviceName, deviceName)
        print("MIDI device ${deviceName} has been setup.")
    }

    fun noteOn(channel: Int, pitch: Int, velocity: Int) {
        println("note on: ${channel}: $pitch")

        midi.sendNoteOn(10, 22, 0)
        midi.sendNoteOn(10, 21, 0)

        if (channel == 10 && pitch == 22) {
            // interaction
            project.value.pulseScene.enabled.value = true
            project.value.leda.enabled.value = true

            project.value.cloudScene.enabled.value = false
            midi.sendNoteOn(10, 22, 127)
        }

        if (channel == 10 && pitch == 21) {
            // idle
            project.value.pulseScene.enabled.value = false
            project.value.leda.enabled.value = false

            project.value.cloudScene.enabled.value = true
            midi.sendNoteOn(10, 21, 127)
        }
    }

    fun noteOff(channel: Int, pitch: Int, velocity: Int) {
    }

    fun controllerChange(channel: Int, number: Int, value: Int) {
        println("controller: ${channel} = ${number}")

        if (channel == 10 && number == 9) {
            project.value.light.luminosity.value = kotlin.math.min(0.2f, value / 127f)
        } else if (channel == 10 && number == 8) {
            project.value.starPattern.color.value = HSV((value / 127f * 360).toInt(), 100, 100).toRGB()
        }
    }
}