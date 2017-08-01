package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.Sketch

class RemoteController(internal var sketch: Sketch) {

    fun processCommand(key: Char) {
        when (key) {
            'i' -> sketch.isStatusViewShown = !sketch.isStatusViewShown
        }
    }
}