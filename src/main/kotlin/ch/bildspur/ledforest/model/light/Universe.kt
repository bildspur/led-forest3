package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.NumberParameter
import com.google.gson.annotations.Expose
import java.awt.Color
import kotlin.math.abs

class Universe(id: Int = 0) {
    companion object {
        @JvmStatic
        val MAX_LUMINANCE = 255
    }

    @NumberParameter("Id")
    @Expose
    var id = DataModel(id)

    @ActionParameter("LEDs", "Select")
    val markLEDs = {
        Sketch.instance.project.value.tubes
                .filter { it.universe.value == this.id.value }
                .forEachLED {
                    it.color.fade(ColorMode.color(250, 0, 100), 0.1f)
                }
    }

    @ActionParameter("LEDs", "Deselect")
    val deselectLEDs = {
        Sketch.instance.project.value.tubes
                .filter { it.universe.value == this.id.value }
                .forEachLED {
                    it.color.fadeB(0f, 0.1f)
                }
    }

    var dmxData: ByteArray
        internal set

    init {
        this.dmxData = ByteArray(512)
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

        return (normValue * MAX_LUMINANCE).toInt().toByte()
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
        return (x - x * k) / (k - abs(x) * 2f * k + 1)
    }

    override fun toString(): String {
        return "Universe (${id.value + 1})"
    }
}