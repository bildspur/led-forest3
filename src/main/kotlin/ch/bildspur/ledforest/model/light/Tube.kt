package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.configuration.PostProcessable
import ch.bildspur.ledforest.ui.properties.*
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.SpaceInformation
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import processing.core.PVector


class Tube(universe: DataModel<Int> = DataModel(0),
           addressStart: DataModel<Int> = DataModel(0),
           @PVectorParameter("Position") @Expose val position: DataModel<PVector> = DataModel(PVector()),
           @PVectorAngleParameter("Rotation") @Expose val rotation: DataModel<PVector> = DataModel(PVector()))
    : LightElement(universe, addressStart, initialLEDCount = 24), PostProcessable {

    var isSelected = DataModel(false)

    @EnumParameter("Origin")
    @Expose
    var origin = DataModel(TubeOrigin.Bottom)

    @Expose
    @NumberParameter("Length")
    val length = DataModel(1.5f)

    @Expose
    @EnumParameter("Tag")
    var tag = DataModel(TubeTag.Interaction)

    init {
        hookPositionListener()
    }

    private fun hookPositionListener() {
        position.onChanged += {
            recalculateLEDPosition()
        }
        rotation.onChanged += {
            recalculateLEDPosition()
        }
    }

    override fun ledPositionByIndex(index: Int): PVector {
        // this can happen because light element is initialized first
        if (length == null) {
            return PVector()
        }

        return SpaceInformation.calculateLEDPosition(index, this)
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
        super.gsonPostProcess()
        hookPositionListener()
        recalculateLEDPosition()
    }
}