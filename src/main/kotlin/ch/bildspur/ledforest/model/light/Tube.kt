package ch.bildspur.ledforest.model.light

import com.google.gson.annotations.Expose
import processing.core.PGraphics
import processing.core.PVector


class Tube(@Expose var universe: Int, ledCount: Int = 0, addressStart: Int = 0, val g: PGraphics, @Expose var position: PVector = PVector(), @Expose var rotation: PVector = PVector()) {
    @Expose var name = "Tube $ledCount"
    @Expose var inverted = false

    val startAddress: Int
        get() = if (leds.isNotEmpty()) leds[0].address else 0

    val endAddress: Int
        get() = if (leds.isNotEmpty()) leds[leds.size - 1].address else 0

    val leds = (0..ledCount).map { LED(g, addressStart + it * LED.LED_ADDRESS_SIZE, g.color(0, 100, 100)) }
}