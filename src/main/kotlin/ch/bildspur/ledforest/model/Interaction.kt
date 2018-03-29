package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class Interaction {
    @Expose
    @BooleanParameter("Single Color Int.")
    var singleColorInteraction = DataModel(false)

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
    @BooleanParameter("Show Interaction Info")
    var showInteractionInfo = DataModel(false)
}