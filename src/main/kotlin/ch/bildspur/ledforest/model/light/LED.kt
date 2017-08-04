package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.model.FadeColor
import processing.core.PGraphics


class LED(g: PGraphics, var address: Int, color: Int) {
    companion object {
        @JvmStatic val LED_ADDRESS_SIZE = 3
    }

    var color: FadeColor = FadeColor(g, color)

}