package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.LightElement

fun List<LightElement>.forEachLED(block: (led: LED) -> Unit) {
    this.flatMap { it.leds }.forEach {
        block(it)
    }
}

fun List<LightElement>.colorizeEach(perElement: Boolean = false, block: (led: LED) -> Unit) {
    if (perElement) {
        this.forEach { e ->
            if (e.leds.isEmpty()) return@forEach

            val centerLed = e.leds[e.leds.size / 2]
            block(centerLed)

            e.leds.forEach {
                it.color.target.set(centerLed.color.target)
                it.color.current.set(centerLed.color.current)
            }
        }
    } else {
        this.flatMap { it.leds }.forEach {
            block(it)
        }
    }
}