package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.pose.Pose
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose

class PoseClassificationConfig {
    @Expose
    var samples = mutableMapOf<Int, MutableList<Pose>>()

    val sampleCount: Int
        get() = samples.map { it.value.size }.sum()

    fun sample(pose: Pose, label: Int) {
        if (label !in samples) {
            samples[label] = mutableListOf()
        }
        samples[label]?.add(pose)
    }

    @StringParameter("Samples", isEditable = false)
    var sampleCountText = DataModel("-")

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
        Sketch.instance.pose.poseClassifier.fit(samples)
    }
}