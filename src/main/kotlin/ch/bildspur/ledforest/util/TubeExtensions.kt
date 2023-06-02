package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.model.LightGroup
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.LightElement

fun List<LightElement>.forEachLED(block: (led: LED) -> Unit) {
    this.flatMap { it.leds }.forEach {
        block(it)
    }
}

private data class LightElementLED(val element: LightElement, val led: LED)

fun List<LightElement>.colorizeEach(
    group: LightGroup = LightGroup.LED,
    block: (element: LightElement, led: LED) -> Unit
) {
    when (group) {
        LightGroup.Universe -> {
            val centerLedPerUniverse = this.groupBy { it.universe.value }
                .map { it.key to it.value.center() }
                .map { it.first to LightElementLED(it.second!!, it.second?.leds?.center()!!) }
                .associate { it }

            // apply block
            centerLedPerUniverse.forEach { block(it.value.element, it.value.led) }

            this.forEach { e ->
                val templateLed = centerLedPerUniverse[e.universe.value]!!.led

                e.leds.forEach {
                    it.color.target.set(templateLed.color.target)
                    it.color.current.set(templateLed.color.current)
                }
            }
        }

        LightGroup.Element -> {
            this.forEach { e ->
                if (e.leds.isEmpty()) return@forEach

                val centerLed = e.leds.center()!!
                block(e, centerLed)

                e.leds.forEach {
                    it.color.target.set(centerLed.color.target)
                    it.color.current.set(centerLed.color.current)
                }
            }
        }

        else -> {
            this.forEach { e ->
                e.leds.forEach { l ->
                    block(e, l)
                }
            }
        }
    }
}