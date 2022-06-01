package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.ledforest.util.SpaceInformation
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
import com.google.gson.annotations.Expose
import processing.core.PVector
import kotlin.math.abs
import kotlin.math.ceil

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
    @PVectorParameter("Interaction Space")
    var interactionSpace = DataModel(PVector(1f, 1f, 1f))

    @Expose
    @PVectorParameter("Mapping Space")
    var mappingSpace = DataModel(PVector(15f, 15f, 10f))

    @ActionParameter("Mapping Space", "Auto Scale")
    val autoScaleInteractionBox = {
        // factor two because interaction box is only half sized
        val scaleFactor = 2.02f

        val ranges = SpaceInformation.calculateTubeDimensions(Sketch.instance.project.value.tubes)

        mappingSpace.value = PVector(
                ceil(abs(ranges.x.low).coerceAtLeast(ranges.x.high) * scaleFactor).toFloat(),
                ceil(abs(ranges.y.low).coerceAtLeast(ranges.y.high) * scaleFactor).toFloat(),
                ceil(abs(ranges.z.low).coerceAtLeast(ranges.z.high) * scaleFactor).toFloat()
        )
    }
}