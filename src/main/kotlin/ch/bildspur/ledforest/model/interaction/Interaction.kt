package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.ActionParameter
import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import com.google.gson.annotations.Expose
import processing.core.PVector

class Interaction {
    @Expose
    @BooleanParameter("Interaction Data Enabled")
    var isInteractionDataEnabled = DataModel(true)

    @Expose
    @BooleanParameter("Leap Interaction Enabled")
    var isLeapInteractionEnabled = DataModel(false)

    @Expose
    @BooleanParameter("Real Sense Interaction Enabled")
    var isRealSenseInteractionEnabled = DataModel(false)

    @Expose
    @PVectorParameter("Interaction Box")
    var interactionBox = DataModel(PVector(15f, 15f, 10f))

    @ActionParameter("Interaction Box", "Auto Scale")
    val autoScaleInteractionBox = {
        // factor two because interaction box is only half sized
        val scaleFactor = 2.02f

        val ranges = Sketch.instance.spaceInformation.calculateTubeDimensions(Sketch.instance.project.value.tubes)

        interactionBox.value = PVector(
                Math.ceil(Math.max(Math.abs(ranges.x.lowValue), ranges.x.highValue) * scaleFactor).toFloat(),
                Math.ceil(Math.max(Math.abs(ranges.y.lowValue), ranges.y.highValue) * scaleFactor).toFloat(),
                Math.ceil(Math.max(Math.abs(ranges.z.lowValue), ranges.z.highValue) * scaleFactor).toFloat()
        )
    }

    @Expose
    @BooleanParameter("Show Interaction Box")
    var showInteractionInfo = DataModel(false)
}