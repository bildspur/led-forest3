package ch.bildspur.ledforest.model.math

import kotlin.math.abs

class OneEuroFilter(
        var tPrev: Float = 0.0f,
        var xPrev: Float = 0.0f,
        var dxPrev: Float = 0.0f,
        var minCutoff: Float = 2.0f,
        var beta: Float = 0.0f,
        var dCutoff: Float = 1.0f
) {

    private fun smoothingFactor(te: Float, cutoff: Float): Float {
        val r = 2 * Math.PI * cutoff * te
        return (r / (r + 1)).toFloat()
    }

    private fun exponentialSmoothing(a: Float, x: Float, xPrev: Float): Float {
        return a * x + (1 - a) * xPrev
    }

    fun filter(t: Float, x: Float): Float {
        val te = t - tPrev

        // filtered derivative of the signal
        val ad = smoothingFactor(te, dCutoff)
        val dx = (x - xPrev) / te
        val dxHat = exponentialSmoothing(ad, dx, dxPrev)

        // filtered signal
        val cutoff: Float = minCutoff + beta * abs(dxHat)
        val a = smoothingFactor(te, cutoff)
        val xHat = exponentialSmoothing(a, x, xPrev)

        // memorize the previous vars
        xPrev = xHat
        dxPrev = dxHat
        tPrev = t
        return xHat
    }

    val value: Float
        get() = xPrev
}