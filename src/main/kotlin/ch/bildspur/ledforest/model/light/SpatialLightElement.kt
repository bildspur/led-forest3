package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.configuration.PostProcessable
import ch.bildspur.ledforest.ui.properties.PVectorAngleParameter
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import com.google.gson.annotations.Expose
import processing.core.PVector

abstract class SpatialLightElement(
    universe: DataModel<Int> = DataModel(0),
    addressStart: DataModel<Int> = DataModel(0),
    @PVectorParameter("Position") @Expose val position: DataModel<PVector> = DataModel(PVector()),
    @PVectorAngleParameter("Rotation") @Expose val rotation: DataModel<PVector> = DataModel(PVector()),
    initialLEDCount: Int = 1
) : LightElement(universe, addressStart, initialLEDCount), PostProcessable {

    private fun hookPositionListener() {
        position.onChanged += {
            recalculateLEDPosition()
        }
        rotation.onChanged += {
            recalculateLEDPosition()
        }
        position.fireLatest()
    }

    fun recalculateLEDPosition() {
        leds.forEachIndexed { index, led ->
            led.position = ledPositionByIndex(index)
        }
    }

    override fun gsonPostProcess() {
        super.gsonPostProcess()
        hookPositionListener()
    }

    abstract val ledLength: Float

    init {
        hookPositionListener()
        recalculateLEDPosition()
    }
}