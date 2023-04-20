package ch.bildspur.ledforest.util

import kotlin.math.roundToLong

class FPSLimiter(var targetFPS: Double) {
    private val tracker = FPSTracker()

    private val targetLatency: Long
        get() = (1000.0 / targetFPS).roundToLong()

    fun limit() {
        // measure
        tracker.update()

        // sleep
        var sleepTime = targetLatency

        if (tracker.latency > sleepTime) {
            sleepTime = tracker.latency - sleepTime
        }

        Thread.sleep(sleepTime)
    }
}