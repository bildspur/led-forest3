package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.util.concurrent.CopyOnWriteArrayList
import javax.xml.crypto.Data

class PulseSceneConfig {
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
    private var templatePulse = Pulse(DataModel(0L))

    @ActionParameter("Pulse", "Send")
    private var sendPulse = {
        val pulse = templatePulse.deepCopy() // templatePulse.copy(startTime = DataModel(System.currentTimeMillis()))
        pulse.startTime.value = System.currentTimeMillis()
        pulses.add(pulse)
    }

    @ActionParameter("Pulse", "Example1")
    private var sendTwoPulses = {
        val start = System.currentTimeMillis()

        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(templatePulse.speed.value),
                width = DataModel(templatePulse.width.value),
                location = DataModel(PVector(4.0f, 0.0f)),
                hue = DataModel(0.0f)
        ))
        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(templatePulse.speed.value),
                width = DataModel(templatePulse.width.value),
                location = DataModel(PVector(-4.0f, 0.0f)),
                hue = DataModel(200.0f)
        ))
    }
}