package ch.bildspur.ledforest.model.pulse

import ch.bildspur.color.RGB
import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ColorParameter
import ch.bildspur.ui.properties.EnumParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.lang.Long.max
import kotlin.math.min

data class Pulse(
    // start parameter
    val startTime: DataModel<Long> = DataModel(0L),
    @Expose @StringParameter("Name") var name: DataModel<String> = DataModel("Pulse"),
    @Expose @NumberParameter("Delay (ms)") var delay: DataModel<Int> = DataModel(0),
    @Expose @PVectorParameter("Location (m)") var location: DataModel<PVector> = DataModel(PVector()),

    // expansion parameter
    @Expose @NumberParameter("Duration (ms)") var duration: DataModel<Float> = DataModel(2000f),
    @Expose @NumberParameter("Distance (m)") var distance: DataModel<Float> = DataModel(10f),
    @Expose @EnumParameter("Expansion Curve") var expansionCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),

    // ring parameter
    @Expose @NumberParameter("Width") var width: DataModel<Float> = DataModel(1f),
    @Expose @EnumParameter("Attack Curve") var attackCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),
    @Expose @EnumParameter("Release Curve") var releaseCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),

    @Expose @ColorParameter("Color") var color: DataModel<RGB> = DataModel(RGB(1.0, 0.0, 0.0, 1.0))
) {

    fun isAlive(timesStamp: Long): Boolean {
        return getProgress(timesStamp) < 1.0f
    }

    /**
     * Returns progression
     */
    fun getProgress(timesStamp: Long): Float {
        return max(0, timesStamp - (startTime.value + delay.value)) / duration.value
    }

    /**
     * Returns the radius of the actual pulse.
     */
    fun getPulseRadius(timesStamp: Long): Float {
        val expansionProgress = expansionCurve.value.method(getProgress(timesStamp))
        return expansionProgress * distance.value
    }

    fun spawn(startTime: Long = System.currentTimeMillis()): Pulse {
        val json = Gson().toJson(this)
        val pulse = Gson().fromJson(json, Pulse::class.java)
        pulse.startTime.value = startTime
        return pulse
    }

    override fun toString(): String {
        return "${name.value}"
    }
}