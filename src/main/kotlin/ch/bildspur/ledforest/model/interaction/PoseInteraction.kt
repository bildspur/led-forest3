package ch.bildspur.ledforest.model.interaction

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.SliderParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose

class PoseInteraction {

    @StringParameter("Pose Count", isEditable = false)
    var poseCount = DataModel("-")

    @Expose
    @NumberParameter("Port")
    var port = DataModel(7400)

    @Expose
    @SliderParameter("Max Delta", 1.0, 100.0, 1.0, snap = true)
    var maxDelta = DataModel(50f)

    @Expose
    @SliderParameter("Min Score", 0.0, 1.0, 0.05, snap = true)
    var minScore = DataModel(0.5f)

    // orientation
    @Expose
    @BooleanParameter("Flip X")
    var flipX = DataModel(false)

    @Expose
    @BooleanParameter("Flip Y")
    var flipY = DataModel(false)

    @Expose
    @BooleanParameter("Flip Z")
    var flipZ = DataModel(false)
}