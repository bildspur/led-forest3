package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class LeapInteraction {
    @Expose
    @BooleanParameter("Single Color Int.")
    var singleColorInteraction = DataModel(false)

    @Expose
    @BooleanParameter("Enable Strobe")
    var isStrobeEnabled = DataModel(false)

    @Expose
    @SliderParameter("Strobe Threshold", 0.0, 1.0)
    var strobeThreshold = DataModel(0.8f)

    @Expose
    @SliderParameter("Int. Distance", 1.0, 200.0)
    var interactionDistance = DataModel(75f)

    @Expose
    @SliderParameter("Hue Start", 0.0, 360.0)
    var hueStart = DataModel(180f)

    @Expose
    @SliderParameter("Hue End", 0.0, 360.0)
    var hueEnd = DataModel(360f)

    @Expose
    @BooleanParameter("Show LeapInteraction Info")
    var showInteractionInfo = DataModel(false)
}