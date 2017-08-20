package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.model.FadeColor


class LED(var address: Int, color: Int) {
    companion object {
        @JvmStatic
        val LED_ADDRESS_SIZE = 3
        @JvmStatic
        val SIZE = 2f
    }

    var color: FadeColor = FadeColor(color)

}