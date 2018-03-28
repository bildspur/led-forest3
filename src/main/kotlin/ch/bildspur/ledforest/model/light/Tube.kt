package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.configuration.PostProcessable
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.*
import ch.bildspur.ledforest.util.ColorMode
import com.google.gson.annotations.Expose
import processing.core.PVector


class Tube(@IntParameter("Universe") @Expose val universe: DataModel<Int> = DataModel(0),
           @IntParameter("Start") @Expose private val addressStart: DataModel<Int> = DataModel(0),
           @PVectorParameter("Position") @Expose val position: DataModel<PVector> = DataModel(PVector()),
           @PVectorAngleParameter("Rotation") @Expose val rotation: DataModel<PVector> = DataModel(PVector()))
    : PostProcessable {

    companion object {
        @JvmStatic
        val WIDTH = 1f
    }

    @BooleanParameter("Inverted")
    @Expose
    var inverted = DataModel(false)

    @Expose
    @IntParameter("LED Count")
    val ledCount = DataModel(24)

    @ActionParameter("LEDs", "Select")
    val markLEDs = {
        leds.forEach {
            it.color.fade(ColorMode.color(250, 100, 100), 0.1f)
        }
    }

    @ActionParameter("LEDs", "Deselect")
    val deselectLEDs = {
        leds.forEach {
            it.color.fadeB(0f, 0.1f)
        }
    }

    val startAddress: Int
        get() = if (leds.isNotEmpty()) leds[0].address else 0

    val endAddress: Int
        get() = if (leds.isNotEmpty()) leds[leds.size - 1].address + 3 else 0

    var leds: List<LED> = emptyList()

    init {
        hookListener()
    }

    fun hookListener() {
        ledCount.onChanged += {
            initLEDs()
        }
        addressStart.onChanged += {
            initLEDs()
        }
        ledCount.fire()
    }

    fun initLEDs() {
        leds = (0 until ledCount.value).map { LED(addressStart.value + it * LED.LED_ADDRESS_SIZE, ColorMode.color(0, 100, 100)) }
    }

    override fun toString(): String {
        return "${universe.value}.$startAddress-$endAddress (${ledCount.value})"
    }

    override fun gsonPostProcess() {
        hookListener()
    }
}