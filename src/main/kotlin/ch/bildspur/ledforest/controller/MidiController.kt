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

        midi.sendControllerChange(10, 8, 127)
        midi.sendNoteOn(10, 23, 127)
    }

    fun noteOn(channel: Int, pitch: Int, velocity: Int) {
        println("note on: ${channel}: $pitch")
    }

    fun noteOff(channel: Int, pitch: Int, velocity: Int) {
        println("note off: ${channel}")
    }

    fun controllerChange(channel: Int, number: Int, value: Int) {
        println("controller: ${channel} = ${number}")

        if (channel == 10 && number == 9) {
            project.value.light.luminosity.value = value / 127f
        } else if (channel == 10 && number == 8) {
            project.value.starPattern.color.value = HSV((value / 127f * 360).toInt(), 100, 100).toRGB()
        }
    }
}