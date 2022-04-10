package ch.bildspur.ledforest.model.pulse

import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.EnumParameter
import com.google.gson.annotations.Expose
import processing.core.PVector

data class Pulse(
        val startTime: DataModel<Long>,
        @Expose @PVectorParameter("Speed") var speed : DataModel<PVector> = DataModel(PVector(1f, 1f, 1f)),
        @Expose @PVectorParameter("Width") var width : DataModel<PVector> = DataModel(PVector(1f, 1f, 1f)),
        @Expose @PVectorParameter("Location") var location : DataModel<PVector> = DataModel(PVector()),
        @Expose @EnumParameter("Attack Curve") var attackCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),
        @Expose @EnumParameter("Release Curve") var releaseCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear)
) {


    fun getPulseRadius(timesStamp: Long): PVector {
        return PVector.mult(PVector.mult(speed.value, 0.001f), (timesStamp - startTime.value).toFloat())
    }
}