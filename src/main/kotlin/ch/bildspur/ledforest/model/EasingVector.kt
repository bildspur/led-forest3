package ch.bildspur.ledforest.model

import processing.core.PVector

class EasingVector(var easing: Float = 0.1f) : PVector() {
    var target: PVector = PVector()

    fun update() {
        add(PVector.sub(target, this).mult(easing))
    }
}