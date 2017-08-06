package ch.bildspur.ledforest.model.light

import com.google.gson.annotations.Expose
import java.awt.Color

class Universe(id: Int) {
    companion object {
        @JvmStatic val MAX_LUMINANCE = 255
    }

    @Expose var id: Int = 0
        internal set
    var dmxData: ByteArray = ByteArray(512)
        internal set

    init {
        this.id = id
    }

    fun stageDmx(tubes: List<Tube>, luminosity: Float, response: Float, trace: Float): ByteArray {
        val data = ByteArray(dmxData.size)

        for (tube in tubes) {
            for (led in tube.leds) {

                val c = Color(led.color.rgbColor)

                // red
                data[led.address] = calculateValue(c.red.toFloat(),
                        dmxData[led.address].toInt() and 0xFF,
                        luminosity, response, trace)

                // green
                data[led.address + 1] = calculateValue(c.green.toFloat(),
                        dmxData[led.address + 1].toInt() and 0xFF,
                        luminosity, response, trace)

                // blue
                data[led.address + 2] = calculateValue(c.blue.toFloat(),
                        dmxData[led.address + 2].toInt() and 0xFF,
                        luminosity, response, trace)
            }
        }

        dmxData = data
        return data
    }

    private fun calculateValue(value: Float, last: Int, luminosity: Float, response: Float, trace: Float): Byte {
        // normalize value
        var normValue = value / MAX_LUMINANCE.toFloat()
        val normLast = last / MAX_LUMINANCE.toFloat()

        // add response
        normValue = normalisedTunableSigmoid(normValue, response)

        // add luminance
        normValue *= luminosity

        // add trace
        normValue = Math.min(1f, normLast * trace + normValue)

        return (normValue * MAX_LUMINANCE).toByte()
    }

    /**
     * Normalised tunable sigmoid function.

     * @param x Normalized x value
     * *
     * @param k Normalized tune value
     * *
     * @return Normalised sigmoid result.
     */
    private fun normalisedTunableSigmoid(x: Float, k: Float): Float {
        return (x - x * k) / (k - Math.abs(x) * 2f * k + 1)
    }
}