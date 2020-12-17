package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.Sketch

class RemoteController(internal var sketch: Sketch) {

    fun processCommand(key: Char) {
        when (key.toLowerCase()) {
            'i' -> sketch.isStatusViewShown = !sketch.isStatusViewShown
            'o' -> sketch.peasy.ortho()
            'p' -> sketch.peasy.perspective()
            '1' -> sketch.peasy.topView()
            '2' -> sketch.peasy.frontView()
            '3' -> sketch.peasy.leftView()
            '4' -> sketch.peasy.rightView()
            '0' -> sketch.peasy.defaultView()
        }
    }
}