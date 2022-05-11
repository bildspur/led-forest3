package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.model.Project
import ch.bildspur.model.DataModel
import dev.atsushieno.ktmidi.*
import kotlinx.coroutines.runBlocking

class MidiController(val project: DataModel<Project>,
                     val deviceName: String = "Midi Fighter Twister 0") : OnMidiReceivedEventListener {
    private val midi = RtMidiAccess()
    private var midiInput: MidiInput? = null

    init {
        if (midi.canDetectStateChanges) {
            println("detecting midi changes")
            midi.stateChanged = {
                println("${it.name} has changed")
                checkForInput()
            }
        }

        checkForInput()
    }

    fun checkForInput() {
        for (input in midi.inputs) {
            if (input.name == deviceName) {
                midiInput?.close()
                setupInput(input)
                break
            }
        }
    }

    fun setupInput(portDetails: MidiPortDetails) {
        midiInput = runBlocking { midi.openInputAsync(portDetails.id) }
        midiInput?.setMessageReceivedListener(this)

        println("Midi Input: ${midiInput}")
    }

    override fun onEventReceived(data: ByteArray, start: Int, length: Int, timestampInNanoseconds: Long) {
        println("message received")
    }
}