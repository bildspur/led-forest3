package ch.bildspur.ledforest.animator

import ch.bildspur.ledforest.model.light.LightElement

class LightAnimator(var light: LightElement? = null) {

    fun update() {
    }

    fun fadeAll(target: Int, easing: Float = 0.1f) {
        light?.leds?.forEach {
            it.color.fade(target, easing)
        }
    }
}