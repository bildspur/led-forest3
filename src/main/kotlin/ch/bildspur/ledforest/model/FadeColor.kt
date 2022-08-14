package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.util.ColorMode
import processing.core.PVector
import java.awt.Color
import kotlin.math.abs


class FadeColor() {
    /* Importent: This is for HSB only! */

    private val minimumDistance = 0.001f

    private val hmax = 360f
    private val smax = 100f
    private val bmax = 100f

    internal var current = PVector()
    internal var target = PVector()
    private var easingVector = PVector()

    constructor(c: Int) : this() {
        current = colorToVector(c)
        target = colorToVector(c)
    }

    fun update() {
        val delta = PVector.sub(target, current)

        // Hue => 360° (can ease to both sides)
        val otherDelta = hmax - abs(delta.x)

        if (abs(otherDelta) < abs(delta.x)) {
            delta.x = otherDelta * if (delta.x < 0) 1 else -1
        }

        // no matrix multiplication possible
        current.x = (current.x + delta.x * easingVector.x) % hmax
        current.x = if (current.x < 0) 360 + current.x else current.x

        current.y += delta.y * easingVector.y
        current.z += delta.z * easingVector.z
    }

    fun set(color: Int) {
        this.current = colorToVector(color)
        this.target = colorToVector(color)
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

    var hue: Float
        get() = current.x
        set(value) {
            current.x = value
            target.x = value
        }

    var saturation: Float
        get() = current.y
        set(value) {
            current.y = value
            target.y = value
        }

    var brightness: Float
        get() = current.z
        set(value) {
            current.z = value
            target.z = value
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

    fun rgbColorWithMapping(brightnessCutoff: Float, brightnessCurve: EasingMethod): Int {
        var valb = current.z / bmax
        if (valb < brightnessCutoff) {
            valb = 0f
        }

        return Color.HSBtoRGB(
            current.x / hmax,
            current.y / smax,
            brightnessCurve.method(valb)
        )
    }

    private fun colorToVector(c: Int): PVector {
        return PVector(ColorMode.hue(c), ColorMode.saturation(c), ColorMode.brightness(c))
    }

    private fun vectorToColor(v: PVector): Int {
        return ColorMode.color(Math.round(v.x), Math.round(v.y), Math.round(v.z))
    }

    fun toJavaFXColor(): javafx.scene.paint.Color {
        return javafx.scene.paint.Color.hsb(
            hue.toDouble(),
            (saturation / smax).toDouble(),
            (brightness / bmax).toDouble()
        )
    }

    override fun toString(): String {
        return "FadeColor (H=$hue S=$saturation B=$brightness)"
    }
}