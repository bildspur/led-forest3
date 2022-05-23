package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.Sketch
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose

class PoseClassificationConfig {
    @StringParameter("Samples", isEditable = false)
    var sampleCount = DataModel("-")

    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @Expose
    @BooleanParameter("Sample")
    var sample = DataModel(false)

    @Expose
    @NumberParameter("Label")
    var label = DataModel(0)

    @ActionParameter("Samples", "Train")
    private val train = {
        sample.value = false
        Sketch.instance.pose.poseClassifier.fit()
    }
}