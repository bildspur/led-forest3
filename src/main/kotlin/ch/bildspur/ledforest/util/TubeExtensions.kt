package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.model.LightGroup
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.LightElement

fun List<LightElement>.forEachLED(block: (led: LED) -> Unit) {
    this.flatMap { it.leds }.forEach {
        block(it)
    }
}

fun List<LightElement>.colorizeEach(group: LightGroup = LightGroup.LED, block: (led: LED) -> Unit) {
    if (group == LightGroup.Universe) {
        val centerLedPerUniverse = this.groupBy { it.universe.value }
            .map { it.key to it.value.center()?.leds?.center() }
            .associate { it }

        // apply block
        centerLedPerUniverse.forEach { block(it.value!!) }

        this.forEach { e ->
            val templateLed = centerLedPerUniverse[e.universe.value]!!

            e.leds.forEach {
                it.color.target.set(templateLed.color.target)
                it.color.current.set(templateLed.color.current)
            }
        }
    } else if (group == LightGroup.Element) {
        this.forEach { e ->
            if (e.leds.isEmpty()) return@forEach

            val centerLed = e.leds.center()!!
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