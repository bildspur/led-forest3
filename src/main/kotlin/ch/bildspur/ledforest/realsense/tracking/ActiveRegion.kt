package ch.bildspur.ledforest.realsense.tracking

import ch.bildspur.ledforest.model.easing.EasingVector

class ActiveRegion(val id: Int) {
    var position = EasingVector(0.1f)

    fun update() {
        // easing
        position.update()
    }
}