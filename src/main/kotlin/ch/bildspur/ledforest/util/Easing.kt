package ch.bildspur.ledforest.util

import processing.core.PConstants.PI

/**
 * Created by cansik on 08.06.17.
 */
object Easing {
    // no easing} no acceleration
    fun linear(t: Float): Float {
        return t
    }

    // accelerating from zero velocity
    fun easeInQuad(t: Float): Float {
        return t * t
    }

    // decelerating to zero velocity
    fun easeOutQuad(t: Float): Float {
        return t * (2 - t)
    }

    // acceleration until halfway} then deceleration
    fun easeInOutQuad(t: Float): Float {
        return if (t < .5) 2 * t * t else -1 + (4 - 2 * t) * t
    }

    // accelerating from zero velocity
    fun easeInCubic(t: Float): Float {
        return t * t * t
    }

    // decelerating to zero velocity
    fun easeOutCubic(time: Float): Float {
        var t = time
        return (--t) * t * t + 1
    }

    // acceleration until halfway} then deceleration
    fun easeInOutCubic(t: Float): Float {
        return if (t < .5) 4 * t * t * t else (t - 1) * (2 * t - 2) * (2 * t - 2) + 1
    }

    // accelerating from zero velocity
    fun easeInQuart(t: Float): Float {
        return t * t * t * t
    }

    // decelerating to zero velocity
    fun easeOutQuart(time: Float): Float {
        var t = time
        return 1 - (--t) * t * t * t
    }

    // acceleration until halfway} then deceleration
    fun easeInOutQuart(time: Float): Float {
        var t = time
        return if (t < .5) 8 * t * t * t * t else 1 - 8 * (--t) * t * t * t
    }

    // accelerating from zero velocity
    fun easeInQuint(t: Float): Float {
        return t * t * t * t * t
    }

    // decelerating to zero velocity
    fun easeOutQuint(time: Float): Float {
        var t = time
        return 1 + (--t) * t * t * t * t
    }

    // acceleration until halfway} then deceleration
    fun easeInOutQuint(time: Float): Float {
        var t = time
        return if (t < .5) 16 * t * t * t * t * t else 1 + 16 * (--t) * t * t * t * t
    }
}