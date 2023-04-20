package ch.bildspur.ledforest.util

class ExponentialMovingAverage(private var avgValue: Float = 0.0f, var alpha: Float = 0.1f) {

    val value: Float
        get() = avgValue

    fun reset(initialValue: Float) {
        avgValue = initialValue
    }

    fun update(value: Float) {
        avgValue -= alpha * (avgValue - value)
    }
}