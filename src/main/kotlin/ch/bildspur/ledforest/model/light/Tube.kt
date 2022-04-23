package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.configuration.PostProcessable
import ch.bildspur.ledforest.ui.properties.*
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.SpaceInformation
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import processing.core.PVector


class Tube(@NumberParameter("Universe") @Expose val universe: DataModel<Int> = DataModel(0),
           @NumberParameter("Start") @Expose val addressStart: DataModel<Int> = DataModel(0),
           @PVectorParameter("Position") @Expose val position: DataModel<PVector> = DataModel(PVector()),
           @PVectorAngleParameter("Rotation") @Expose val rotation: DataModel<PVector> = DataModel(PVector()))
    : PostProcessable {

    var isSelected = DataModel(false)

    @Expose
    @StringParameter("Name")
    var name = DataModel("")

    @EnumParameter("Origin")
    @Expose
    var origin = DataModel(TubeOrigin.Bottom)

    @Expose
    @NumberParameter("Length")
    val length = DataModel(1.5f)

    @Expose
    @NumberParameter("LED Count")
    val ledCount = DataModel(24)

    @Expose
    @EnumParameter("Tag")
    var tag = DataModel(TubeTag.Interaction)

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
        get() = if (leds.isNotEmpty()) leds[leds.size - 1].address else 0

    var leds: List<LED> = emptyList()

    init {
        hookListener()
    }

    private fun hookListener() {
        ledCount.onChanged += {
            initLEDs()
        }
        addressStart.onChanged += {
            initLEDs()
        }
        position.onChanged += {
            recalculateLEDPosition()
        }
        rotation.onChanged += {
            recalculateLEDPosition()
        }
        ledCount.fire()
    }

    fun initLEDs() {
        leds = (0 until ledCount.value).map {
            LED(addressStart.value + it * LED.LED_ADDRESS_SIZE,
                ColorMode.color(0, 100, 100),
                SpaceInformation.calculateLEDPosition(it, this)
            )
        }
    }

    fun recalculateLEDPosition() {
        leds.forEachIndexed { index, led ->
            led.position = SpaceInformation.calculateLEDPosition(index, this)
        }
    }

    val ledLength: Float
        get() = length.value / ledCount.value

    override fun toString(): String {
        return "${name.value} ${universe.value + 1}.${startAddress + 1}-${endAddress + 1} (${ledCount.value})"
    }

    override fun gsonPostProcess() {
        hookListener()
    }
}