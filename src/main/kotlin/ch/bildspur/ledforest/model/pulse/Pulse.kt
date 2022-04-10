package ch.bildspur.ledforest.model.pulse

import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.EnumParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import processing.core.PVector

data class Pulse(
        val startTime: DataModel<Long> = DataModel(0L),
        @Expose @NumberParameter("Speed") var speed: DataModel<Float> = DataModel(1f),
        @Expose @NumberParameter("Width") var width: DataModel<Float> = DataModel(1f),
        @Expose @PVectorParameter("Location") var location: DataModel<PVector> = DataModel(PVector()),

        @Expose @EnumParameter("Attack Curve") var attackCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),
        @Expose @EnumParameter("Release Curve") var releaseCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),

        @Expose @SliderParameter("Hue", 0.0, 360.0, 1.0) var hue: DataModel<Float> = DataModel(0.0f),
        @Expose @SliderParameter("Saturation", 0.0, 100.0, 1.0) var saturation: DataModel<Float> = DataModel(100.0f)
) {


    fun getPulseRadius(timesStamp: Long): Float {
        return (speed.value * 0.001f) * (timesStamp - startTime.value).toFloat()
    }

    fun deepCopy(): Pulse {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, Pulse::class.java)
    }
}