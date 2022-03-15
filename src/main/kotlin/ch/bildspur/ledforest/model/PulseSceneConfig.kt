package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.GroupParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.util.concurrent.CopyOnWriteArrayList

class PulseSceneConfig {
    data class PulseSettings(@Expose @PVectorParameter("Speed") var speed : DataModel<PVector> = DataModel(PVector(1f, 1f, 1f)),
                             @Expose @PVectorParameter("Width") var width : DataModel<PVector> = DataModel(PVector(1f, 1f, 1f)),
                             @Expose @PVectorParameter("Location") var location : DataModel<PVector> = DataModel(PVector()))

    @StringParameter("Pulse Count", isEditable = false)
    var pulseCount = DataModel("-")

    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    val pulses = CopyOnWriteArrayList<Pulse>()

    @GroupParameter("Pulse")
    private var templatePulse = PulseSettings()

    @ActionParameter("Pulse", "Send")
    private var sendPulse = {
        val pulse = Pulse(
                System.currentTimeMillis(),
                templatePulse.speed.value.copy(),
                templatePulse.width.value.copy(),
                templatePulse.location.value.copy()
        )
        pulses.add(pulse)
    }
}