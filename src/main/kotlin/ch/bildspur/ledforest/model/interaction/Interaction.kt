package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import com.google.gson.annotations.Expose
import processing.core.PVector

class Interaction {
    @Expose
    @BooleanParameter("Interaction On")
    var isInteractionOn = DataModel(true)

    @Expose
    @BooleanParameter("Leap Interaction")
    var isLeapInteraction = DataModel(true)

    @Expose
    @BooleanParameter("Real Sense Interaction")
    var isRealSenseInteraction = DataModel(false)

    @Expose
    @PVectorParameter("Interaction Box")
    var interactionBox = DataModel(PVector(150f, 150f, 100f))

    @Expose
    @BooleanParameter("Show Interaction Box")
    var showInteractionInfo = DataModel(false)
}