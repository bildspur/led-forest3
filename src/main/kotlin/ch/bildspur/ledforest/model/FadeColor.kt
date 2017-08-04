package ch.bildspur.ledforest.model

import processing.core.PGraphics
import processing.core.PVector
import java.awt.Color


class FadeColor(g: PGraphics) {
    /* Importent: This is for HSB only! */

    internal val minimumDistance = 0.001f

    internal val hmax = 360f
    internal val smax = 100f
    internal val bmax = 100f

    internal var current = PVector()
    internal var target = PVector()
    internal var easingVector = PVector()

    var graphics: PGraphics
        internal set

    init {
        this.graphics = g
    }

    constructor(g: PGraphics, c: Int) : this(g) {
        current = colorToVector(c)
        target = colorToVector(c)
    }

    fun update() {
        val delta = target.copy().sub(current)

        // Hue => 360° (can ease to both sides)
        val otherDelta = hmax - Math.abs(delta.x)

        if (Math.abs(otherDelta) < Math.abs(delta.x)) {
            delta.x = otherDelta * if (delta.x < 0) 1 else -1
        }

        // no matrix multiplication possible
        current.x = (current.x + delta.x * easingVector.x) % hmax
        current.x = if (current.x < 0) 360 + current.x else current.x

        current.y += delta.y * easingVector.y
        current.z += delta.z * easingVector.z
    }

    fun fade(t: Int, easing: Float) {
        easingVector = PVector(easing, easing, easing)
        this.target = colorToVector(t)
    }

    fun fadeH(h: Float, easing: Float) {
        easingVector.x = easing
        target.x = h
    }

    fun fadeS(s: Float, easing: Float) {
        easingVector.y = easing
        target.y = s
    }

    fun fadeB(b: Float, easing: Float) {
        easingVector.z = easing
        target.z = b
    }

    val isFading: Boolean
        get() = PVector.sub(target, current).mag() > minimumDistance

    var color: Int
        get() = vectorToColor(current)
        set(c) {
            current = colorToVector(c)
            target = colorToVector(c)
        }

    val rgbColor: Int
        get() = Color.HSBtoRGB(current.x / hmax, current.y / smax, current.z / bmax)

    private fun colorToVector(c: Int): PVector {
        return PVector(graphics.hue(c), graphics.saturation(c), graphics.brightness(c))
    }

    private fun vectorToColor(v: PVector): Int {
        return graphics.color(Math.round(v.x), Math.round(v.y), Math.round(v.z))
    }
}