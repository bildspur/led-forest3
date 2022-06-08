package ch.bildspur.ledforest.util

import ch.bildspur.color.HSV
import ch.bildspur.math.pow
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ColorMixer {
    private val huesAndWeights = mutableListOf<ColorUtil.HueAndWeight>()
    private var totalSaturation = 0f
    private var totalBrightness = 0f

    private var applyCount = 0

    fun init() {
        huesAndWeights.clear()
        totalSaturation = 0f
        totalBrightness = 0f
        applyCount = 0
    }

    fun addColor(hue: Float, saturation: Float, brightness: Float, hueWeight: Float = 1.0f) {
        huesAndWeights.add(ColorUtil.HueAndWeight(hue, hueWeight))
        totalSaturation += pow(saturation, 2f)
        totalBrightness += brightness

        applyCount++
    }

    val mixedColor: HSV
        get() = HSV(
                ColorUtil.mixHueWeighted(huesAndWeights).roundToInt(),
                sqrt(totalSaturation / max(1, applyCount)).roundToInt(),
                totalBrightness.limit(0f, 100f).roundToInt())
}