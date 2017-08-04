package ch.bildspur.ledforest.model.light

import processing.core.PGraphics
import processing.core.PVector


class Tube(var universe: Int, ledCount: Int = 0, addressStart: Int = 0, val g: PGraphics, var position: PVector = PVector(), var rotation: PVector = PVector()) {
    var name = "Tube $ledCount"
    var inverted = false

    val startAddress: Int
        get() = if (leds.isNotEmpty()) leds[0].address else 0

    val endAddress: Int
        get() = if (leds.isNotEmpty()) leds[leds.size - 1].address else 0

    val leds = (0..ledCount).map { LED(g, addressStart + it * LED.LED_ADDRESS_SIZE, g.color(0, 100, 100)) }
}