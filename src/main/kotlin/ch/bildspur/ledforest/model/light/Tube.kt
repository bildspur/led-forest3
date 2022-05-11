package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.util.SpaceInformation
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.EnumParameter
import ch.bildspur.ui.properties.NumberParameter
import com.google.gson.annotations.Expose
import processing.core.PVector


class Tube(universe: DataModel<Int> = DataModel(0),
           addressStart: DataModel<Int> = DataModel(0),
           position: DataModel<PVector> = DataModel(PVector()),
           rotation: DataModel<PVector> = DataModel(PVector()))
    : SpatialLightElement(universe, addressStart, position, rotation, initialLEDCount = 24) {

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

    override fun ledPositionByIndex(index: Int): PVector {
        // this can happen because light element is initialized first
        if (length == null) {
            return PVector()
        }

        return SpaceInformation.calculateLEDPosition(index, this)
    }

    val ledLength: Float
        get() = length.value / ledCount.value
}