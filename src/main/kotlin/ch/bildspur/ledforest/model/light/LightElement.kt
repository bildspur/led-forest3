package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.configuration.PostProcessable
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import processing.core.PVector

abstract class LightElement(
        @NumberParameter("Universe") @Expose val universe: DataModel<Int> = DataModel(0),
        @NumberParameter("Start") @Expose val addressStart: DataModel<Int> = DataModel(0),
        initialLEDCount: Int = 1
) : PostProcessable {
    @Expose
    @StringParameter("Name")
    var name = DataModel("")

    @Expose
    @NumberParameter("LED Count")
    val ledCount = DataModel(initialLEDCount)

    val startAddress: Int
        get() = if (leds.isNotEmpty()) leds[0].address else 0

    val endAddress: Int
        get() = if (leds.isNotEmpty()) leds[leds.size - 1].address else 0

    var leds: List<LED> = emptyList()

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

    fun initLEDs() {
        leds = (0 until ledCount.value).map {
            LED(addressStart.value + it * LED.LED_ADDRESS_SIZE,
                    ColorMode.color(0, 100, 100),
                    ledPositionByIndex(it)
            )
        }
    }

    private fun hookListener() {
        ledCount.onChanged += {
            initLEDs()
        }
        addressStart.onChanged += {
            initLEDs()
        }
        ledCount.fire()
    }

    internal open fun ledPositionByIndex(index: Int): PVector {
        return PVector()
    }

    override fun toString(): String {
        return "${name.value} (${universe.value + 1}.${startAddress + 1}-${endAddress + 1} / ${ledCount.value})"
    }

    override fun gsonPostProcess() {
        hookListener()
    }

    init {
        hookListener()
    }
}