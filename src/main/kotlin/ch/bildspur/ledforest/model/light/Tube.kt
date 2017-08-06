package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.util.ColorMode
import com.google.gson.annotations.Expose
import processing.core.PVector


class Tube(@Expose var universe: Int,
           @Expose var ledCount: Int = 0,
           @Expose var addressStart: Int = 0,
           @Expose var position: PVector = PVector(),
           @Expose var rotation: PVector = PVector()) {

    @Expose var name = "Tube"
    @Expose var inverted = false

    val startAddress: Int
        get() = if (leds.isNotEmpty()) leds[0].address else 0

    val endAddress: Int
        get() = if (leds.isNotEmpty()) leds[leds.size - 1].address else 0

    val leds = (0..ledCount).map { LED(addressStart + it * LED.LED_ADDRESS_SIZE, ColorMode.color(0, 100, 100)) }

    override fun toString(): String {
        return "$name ($ledCount)"
    }
}