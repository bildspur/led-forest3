package ch.bildspur.floje.controller

import ch.bildspur.floje.Sketch

class RemoteController(internal var sketch: Sketch) {

    fun processCommand(key: Char) {
        when (key) {
            'i' -> sketch.isStatusViewShown = !sketch.isStatusViewShown
        }
    }
}