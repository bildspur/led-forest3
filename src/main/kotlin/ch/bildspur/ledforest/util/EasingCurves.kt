package ch.bildspur.ledforest.util

import com.jogamp.opengl.math.FloatUtil.sin
import processing.core.PApplet.*
import processing.core.PConstants.PI

/**
 * Created by cansik on 08.06.17.
 */
object EasingCurves {
    fun easeInSine(x: Float): Float {
        return 1 - cos((x * PI) / 2f)
    }

    fun easeOutSine(x: Float): Float {
        return sin((x * PI) / 2f)
    }

    fun easeInOutSine(x: Float): Float {
        return -(cos(PI * x) - 1) / 2f
    }

    // no easing} no acceleration
    fun linear(t: Float): Float {
        return t
    }

    fun step(t: Float): Float {
        if(t > 0.5f) return 1.0f
        return 0.0f
    }

    fun sinePulse(t: Float): Float {
        return 0.5f * (1 + sin(2 * PI * t - (PI / 2))) * sin(t * 19)
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

    fun easeInCirc(x: Float): Float {
        return 1 - sqrt(1 - pow(x, 2f))
    }
}