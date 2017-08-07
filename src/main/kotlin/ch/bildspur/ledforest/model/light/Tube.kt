package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.ui.properties.ActionParameter
import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.IntParameter
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.ledforest.util.ColorMode
import com.google.gson.annotations.Expose
import processing.core.PVector


class Tube(@IntParameter("Universe") @Expose var universe: Int,
           @Expose var addressStart: Int = 0,
           @PVectorParameter("Position") @Expose var position: PVector = PVector(),
           @PVectorParameter("Rotation") @Expose var rotation: PVector = PVector()) {

    @BooleanParameter("Inverted") @Expose var inverted = false

    @Expose
    @IntParameter("LED Count")
    var ledCount: Int = 0
        set(value) {
            field = value
            initLEDs()
        }

    @ActionParameter("All LEDs", "Mark")
    val markLEDs = {
        leds.forEach {
            it.color.fadeH(255f, 0.1f)
        }
    }

    val startAddress: Int
        get() = if (leds.isNotEmpty()) leds[0].address else 0

    val endAddress: Int
        get() = if (leds.isNotEmpty()) leds[leds.size - 1].address else 0

    var leds: List<LED> = emptyList()

    init {
        initLEDs()
    }

    fun initLEDs() {
        leds = (0..ledCount).map { LED(addressStart + it * LED.LED_ADDRESS_SIZE, ColorMode.color(0, 100, 100)) }
    }

    override fun toString(): String {
        return "$universe.$startAddress-$endAddress ($ledCount)"
    }
}