package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube

fun List<Tube>.forEachLED(block: (led: LED) -> Unit) {
    this.flatMap { it.leds }.forEach {
        block(it)
    }
}