package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.LightElement

fun List<LightElement>.forEachLED(block: (led: LED) -> Unit) {
    this.flatMap { it.leds }.forEach {
        block(it)
    }
}