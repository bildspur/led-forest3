package ch.bildspur.floje.util

import processing.core.PConstants.PI

/**
 * Created by cansik on 08.06.17.
 */
object Easing {
    // t = current time, b = start value, c = change in value, d = duration
    fun easeInQuad(t: Float, b: Float, c: Float, d: Float): Float {
        var t = t
        t /= d
        return c * t * t + b
    }

    fun linearTween(t: Float, b: Float, c: Float, d: Float): Float {
        return c * t / d + b
    }

    fun easeOutQuad(t: Float, b: Float, c: Float, d: Float): Float {
        var t = t
        t /= d
        return -c * t * (t - 2) + b
    }

    fun easeInSine(t: Float, b: Float, c: Float, d: Float): Float {
        return Math.round(-c * Math.cos(t / d * (PI / 2.0)) + c.toDouble() + b.toDouble()).toFloat()
    }

    fun easeOutSine(t: Float, b: Float, c: Float, d: Float): Float {
        return Math.round(c * Math.sin(t / d * (PI / 2.0)) + b).toFloat()
    }
}