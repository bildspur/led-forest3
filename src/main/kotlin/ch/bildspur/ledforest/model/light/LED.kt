package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.model.FadeColor
import processing.core.PVector


class LED(var address: Int,
          color: Int,
          var position : PVector = PVector()) {
    companion object {
        @JvmStatic
        val LED_ADDRESS_SIZE = 3
    }

    var color: FadeColor = FadeColor(color)
}