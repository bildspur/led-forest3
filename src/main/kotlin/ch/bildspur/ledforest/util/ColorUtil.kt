package ch.bildspur.ledforest.util

object ColorUtil {

    fun mixHueWeighted(values: List<HueAndWeight>): Float {
        val totalWeight = values.map { it.weight }.sum()
        var hueSum = 0f

        for (value in values) {
            var hval = value.hue
            if (hval >= 180.0)
                hval -= 360.0f

            hueSum += (hval * (value.weight / totalWeight))
        }

        if (hueSum < 0)
            hueSum += 360f

        return hueSum
    }

    data class HueAndWeight(val hue: Float, val weight: Float)
}