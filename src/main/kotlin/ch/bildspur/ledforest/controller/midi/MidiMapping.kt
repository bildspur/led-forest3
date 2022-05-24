package ch.bildspur.ledforest.controller.midi

import ch.bildspur.ledforest.model.Project
import themidibus.MidiBus

abstract class MidiMapping(var deviceName: String) {

    abstract fun attach(project: Project, midi: MidiBus)
    abstract fun detach(project: Project)

    abstract fun noteOn(project: Project, midi: MidiBus, channel: Int, pitch: Int, velocity: Int)
    abstract fun noteOff(project: Project, midi: MidiBus, channel: Int, pitch: Int, velocity: Int)
    abstract fun controllerChange(project: Project, midi: MidiBus, channel: Int, number: Int, value: Int)

    fun isDeviceAvailable(devices: Set<String>): Boolean {
        return devices.contains(deviceName)
    }
}