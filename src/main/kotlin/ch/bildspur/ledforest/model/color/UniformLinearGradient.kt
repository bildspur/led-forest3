package ch.bildspur.ledforest.model.color

import ch.bildspur.color.RGB
import ch.bildspur.math.pow
import java.util.*
import kotlin.math.sqrt

class UniformLinearGradient(vararg colorSteps: RGB) {
    private val map = TreeMap<Float, RGB>()

    init {
        if (colorSteps.isEmpty()) {
            throw java.lang.Exception("At least one color is needed for gradient.")
        }

        if (colorSteps.size == 1) {
            map[0.0f] = colorSteps[0]
            map[1.0f] = colorSteps[0]
        } else {
            colorSteps.forEachIndexed { index, color ->
                val i = index.toFloat() / (colorSteps.size - 1)
                map[i] = color
            }
        }
    }

    fun color(x: Float): RGB {
        val xMin = map.floorKey(x)
        val xMax = map.ceilingKey(x)

        val start = map[xMin]
        val end = map[xMax]

        return gradientColor(x, xMin, xMax, start!!, end!!)
    }

    private fun gradientColor(x: Float, minX: Float, maxX: Float,
                              from: RGB, to: RGB): RGB {
        val range = maxX - minX

        if (range == 0.0f) {
            return from
        }

        val p = 1.0f - (x - minX) / range

        return RGB(
                sqrt(pow(from.r * p, 2f) + pow(to.r * (1 - p), 2f)).toInt(),
                sqrt(pow(from.g * p, 2f) + pow(to.g * (1 - p), 2f)).toInt(),
                sqrt(pow(from.b * p, 2f) + pow(to.b * (1 - p), 2f)).toInt(),
                1.0f
        )
    }
}