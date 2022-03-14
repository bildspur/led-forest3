package ch.bildspur.ledforest.model.pulse

import processing.core.PVector

class Pulse(val startTime : Long,
            val speed : PVector = PVector(1f, 1f, 1f),
            val width : PVector = PVector(1f, 1f, 1f),
            val location : PVector = PVector()) {

}