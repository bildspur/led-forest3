package ch.bildspur.ledforest.util

import ch.bildspur.event.Event

class DeBouncer<T : Any>(var delay: Long, defaultValue: T) {
    private var lastTimeStamp = 0L
    private var lastValue: T = defaultValue
    var currentValue: T = defaultValue

    val onChanged = Event<T>()

    fun update(value: T): T {
        if (value != lastValue) {
            lastTimeStamp = System.currentTimeMillis()
        }

        if ((System.currentTimeMillis() - lastTimeStamp) > delay) {
            if (value != currentValue) {
                currentValue = value
                onChanged(value)
            }
        }

        lastValue = value
        return currentValue
    }
}