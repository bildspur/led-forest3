package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.GroupParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.util.concurrent.CopyOnWriteArrayList

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
        pulses.add(templatePulse.spawn())
    }

  

    @ActionParameter("Pulse", "center")
    private var center = {
        val start = System.currentTimeMillis()

        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(0.0f, 0.0f)),
                hue = DataModel(67.0f),
                attackCurve = DataModel(EasingMethod.EaseInQuad),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
      
    }

    @ActionParameter("Pulse", "leftToRight")
    private var leftToRight = {
        val start = System.currentTimeMillis()

        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(-4.0f, 0.0f)),
                hue = DataModel(67.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
      
    }

    @ActionParameter("Pulse", "rightToLeft")
    private var rightToLeft = {
        val start = System.currentTimeMillis()

        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(4.0f, 0.0f)),
                hue = DataModel(67.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
      
    }

   

    @ActionParameter("Pulse", "fast")
    private var fast = {
        val start = System.currentTimeMillis()

        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(6.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(0.0f, 0.0f)),
                hue = DataModel(67.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
      
    }

    @ActionParameter("Pulse", "slow")
    private var slow = {
        val start = System.currentTimeMillis()

        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(1.5f),
                width = DataModel(2.0f),
                location = DataModel(PVector(0.0f, 0.0f)),
                hue = DataModel(67.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
      
    }
    @ActionParameter("Pulse", "TwoPulses")
    private var sendTwoPulses = {
        val start = System.currentTimeMillis()

        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(4.0f, 0.0f)),
                hue = DataModel(0.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(-4.0f, 0.0f)),
                hue = DataModel(200.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
    }
    @ActionParameter("Pulse", "crazy")
    private var crazy = {
        val start = System.currentTimeMillis()

        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(4.0f, 0.0f)),
                hue = DataModel(0.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(-4.0f, 0.0f)),
                hue = DataModel(90.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(0.0f, 4.0f)),
                hue = DataModel(180.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
        pulses.add(Pulse(startTime = DataModel(start),
                speed = DataModel(3.0f),
                width = DataModel(2.0f),
                location = DataModel(PVector(0.0f, -4.0f)),
                hue = DataModel(270.0f),
                attackCurve = DataModel(EasingMethod.Step),
                releaseCurve = DataModel(EasingMethod.Linear)
        ))
    }


   
}