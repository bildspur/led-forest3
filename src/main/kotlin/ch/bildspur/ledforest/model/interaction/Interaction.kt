package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
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
    @BooleanParameter("Pose Interaction Enabled")
    var isPoseInteractionEnabled = DataModel(false)

    @Expose
    @PVectorParameter("Interaction Box")
    var interactionBox = DataModel(PVector(15f, 15f, 10f))

    @ActionParameter("Interaction Box", "Auto Scale")
    val autoScaleInteractionBox = {
        // factor two because interaction box is only half sized
        val scaleFactor = 2.02f

        val ranges = Sketch.instance.spaceInformation.calculateTubeDimensions(Sketch.instance.project.value.tubes)

        interactionBox.value = PVector(
                Math.ceil(Math.max(Math.abs(ranges.x.low), ranges.x.high) * scaleFactor).toFloat(),
                Math.ceil(Math.max(Math.abs(ranges.y.low), ranges.y.high) * scaleFactor).toFloat(),
                Math.ceil(Math.max(Math.abs(ranges.z.low), ranges.z.high) * scaleFactor).toFloat()
        )
    }
}