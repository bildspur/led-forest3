package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.util.concurrent.CopyOnWriteArrayList

class PulseSceneConfig {
    data class PulseSettings(@Expose @PVectorParameter("Speed") var speed : DataModel<PVector> = DataModel(PVector(1f, 1f, 1f)),
                             @Expose @PVectorParameter("Width") var width : DataModel<PVector> = DataModel(PVector(1f, 1f, 1f)),
                             @Expose @PVectorParameter("Location") var location : DataModel<PVector> = DataModel(PVector()),
                             @Expose @EnumParameter("Attack Curve") var attackCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear),
                             @Expose @EnumParameter("Release Curve") var releaseCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear)
    )

    @StringParameter("Pulse Count", isEditable = false)
    var pulseCount = DataModel("-")

    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @Expose
    @BooleanParameter("Visualize Pulses")
    var visualize = DataModel(false)

    val pulses = CopyOnWriteArrayList<Pulse>()

    @GroupParameter("Pulse")
    private var templatePulse = PulseSettings()

    @ActionParameter("Pulse", "Send")
    private var sendPulse = {
        val pulse = Pulse(
                System.currentTimeMillis(),
                templatePulse.speed.value.copy(),
                templatePulse.width.value.copy(),
                templatePulse.location.value.copy(),
                templatePulse.attackCurve.value,
                templatePulse.releaseCurve.value
        )
        pulses.add(pulse)
    }
}