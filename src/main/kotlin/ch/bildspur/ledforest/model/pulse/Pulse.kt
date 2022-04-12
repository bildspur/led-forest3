package ch.bildspur.ledforest.model.pulse

import ch.bildspur.color.RGB
import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ColorParameter
import ch.bildspur.ui.properties.EnumParameter
import ch.bildspur.ui.properties.NumberParameter
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.lang.Long.max

data class Pulse(
        val startTime: DataModel<Long> = DataModel(0L),

        @Expose @NumberParameter("Delay (ms)") var delay: DataModel<Int> = DataModel(0),

        @Expose @NumberParameter("Speed") var speed: DataModel<Float> = DataModel(1f),
        @Expose @NumberParameter("Width") var width: DataModel<Float> = DataModel(1f),
        @Expose @PVectorParameter("Location") var location: DataModel<PVector> = DataModel(PVector()),

        @Expose @EnumParameter("Attack Curve") var attackCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),
        @Expose @EnumParameter("Release Curve") var releaseCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),

        @Expose @ColorParameter("Color")  var color: DataModel<RGB> = DataModel(RGB(1.0, 0.0, 0.0, 1.0))
) {


    fun getPulseRadius(timesStamp: Long): Float {
        val delta = max(0, timesStamp - (startTime.value + delay.value))

        return (speed.value * 0.001f) * delta.toFloat()
    }

    fun spawn(startTime: Long = System.currentTimeMillis()): Pulse {
        val json = Gson().toJson(this)
        val pulse = Gson().fromJson(json, Pulse::class.java)
        pulse.startTime.value = startTime
        return pulse
    }
}