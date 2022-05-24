package ch.bildspur.ledforest.controller.midi

import ch.bildspur.ledforest.model.Project
import ch.bildspur.model.DataModel
import themidibus.MidiBus
import javax.sound.midi.MidiDevice
import kotlin.concurrent.thread

class MidiController(val project: DataModel<Project>) {
    private var running = true

    private var midi: MidiBus = MidiBus(this, -1, -1)
    private var mapping: MidiMapping = XTouchMiniMapping()

    private var isInitialized = false

    val midiMappings = listOf(
        XTouchMiniMapping()
    )

    init {
        project.onChanged += {
            stopInput()
        }

        thread(start = true, isDaemon = true) {
            while (running) {
                checkForInput()
                Thread.sleep(2000)
            }
        }
    }

    private fun checkForInput() {
        // if already a midi input selected
        if (isInitialized) {
            // todo: check for new devices and change
            return
        }

        val inputs = MidiBus.availableInputs().toSet()
        val map = midiMappings.firstOrNull { it.isDeviceAvailable(inputs) }

        if (map != null) {
            mapping = map
            setupInput()
        }
    }

    private fun setupInput() {
        midi.addInput(mapping.deviceName)
        midi.addOutput(mapping.deviceName)
        mapping.attach(project.value, midi)
        isInitialized = true
        println("MIDI device ${mapping.deviceName} has been setup.")
    }

    private fun stopInput() {
        println("MIDI device ${mapping.deviceName} disconnected.")
        mapping.detach(project.value)
        midi.removeInput(0)
        midi.removeOutput(0)
        isInitialized = false
    }

    fun noteOn(channel: Int, pitch: Int, velocity: Int) {
        mapping.noteOn(project.value, midi, channel, pitch, velocity)
    }

    fun noteOff(channel: Int, pitch: Int, velocity: Int) {
        mapping.noteOff(project.value, midi, channel, pitch, velocity)
    }

    fun controllerChange(channel: Int, number: Int, value: Int) {
        println("controller changed")
        mapping.controllerChange(project.value, midi, channel, number, value)

    }
}