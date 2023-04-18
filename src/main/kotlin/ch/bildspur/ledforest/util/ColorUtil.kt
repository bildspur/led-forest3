package ch.bildspur.ledforest.util

import ch.bildspur.color.RGB

object ColorUtil {

    fun mixHueWeighted(values: List<HueAndWeight>): Float {
        val totalWeight = values.map { it.weight }.sum()
        var hueSum = 0f

        if (totalWeight < 0.0001)
            return hueSum

        for (value in values) {
            var hval = value.hue
            if (hval >= 180.0)
                hval -= 360.0f

            hueSum += hval * (value.weight / totalWeight)
        }

        if (hueSum < 0)
            hueSum += 360f

        return hueSum.limit(0f, 360f)
    }

    data class HueAndWeight(val hue: Float, val weight: Float)
}

fun RGB.Companion.parseRgbHex(hex: String): RGB {
    var radix = hex.lowercase().trim().replace("#", "0x")

    if (!radix.startsWith("0x")) {
        radix = "0x${radix}"
    }

    var value = Integer.decode(radix)

    // add alpha
    value = value or (0xFF shl 24)

    return fromInt(value)
}

fun RGB.toRgbHexString(): String {
    return this.toPackedInt().toRgbString().lowercase()
}

fun Int.toRgbString(): String =
    "#${red.toStringComponent()}${green.toStringComponent()}${blue.toStringComponent()}".uppercase()

fun Int.toArgbString(): String =
    "#${alpha.toStringComponent()}${red.toStringComponent()}${green.toStringComponent()}${blue.toStringComponent()}".uppercase()

private fun Int.toStringComponent(): String =
    this.toString(16).let { if (it.length == 1) "0${it}" else it }

inline val Int.alpha: Int
    get() = (this shr 24) and 0xFF

inline val Int.red: Int
    get() = (this shr 16) and 0xFF

inline val Int.green: Int
    get() = (this shr 8) and 0xFF

inline val Int.blue: Int
    get() = this and 0xFF