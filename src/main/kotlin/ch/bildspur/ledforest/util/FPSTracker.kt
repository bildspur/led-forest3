package ch.bildspur.ledforest.util

import kotlin.math.max

open class FPSTracker(private var framesToSkip: Int = 1) {
    private var lastTimeStamp = 0L
    private var currentFPS = 0.0
    private var currentLatency = 0L

    private var fpsEma = ExponentialMovingAverage()
    private var latencyEma = ExponentialMovingAverage()

    private var isFirstTime = true

    val fps: Double
        get() = currentFPS

    val latency: Long
        get() = currentLatency

    val averageFPS: Double
        get() = fpsEma.value.toDouble()

    val averageLatency: Long
        get() = latencyEma.value.toLong()

    fun update() {
        if (framesToSkip > 0) {
            if (framesToSkip == 1) {
                lastTimeStamp = time()
            }

            framesToSkip--
            return
        }

        // update fps
        val ts = time()
        val deltaTime = ts - lastTimeStamp

        currentLatency = deltaTime
        currentFPS = timeToFPS(deltaTime)

        if (isFirstTime) {
            fpsEma.reset(currentFPS.toFloat())
            latencyEma.reset(currentLatency.toFloat())
            isFirstTime = false
        } else {
            fpsEma.update(currentFPS.toFloat())
            latencyEma.update(currentLatency.toFloat())
        }

        lastTimeStamp = ts
    }

    protected open fun time(): Long {
        return System.currentTimeMillis()
    }

    protected open fun timeToFPS(delta: Long): Double {
        return 1000.0 / max(1L, delta)
    }
}