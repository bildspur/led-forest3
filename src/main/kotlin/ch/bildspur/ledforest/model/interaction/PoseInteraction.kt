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
    @BooleanParameter("Debug")
    var isDebug = DataModel(false)

    @Expose
    @BooleanParameter("Show Raw Poses")
    var showRawPoses = DataModel(false)

    @Expose
    @BooleanParameter("Show Tracked Poses")
    var showTrackedPoses = DataModel(false)

    @Expose
    @NumberParameter("Port")
    var port = DataModel(7400)

    @Expose
    @SliderParameter("Min Score", 0.0, 5.0, 0.1, snap = true)
    var minScore = DataModel(2.0f)

    @Expose
    @NumberParameter("Tracking Speed", "FPS")
    var trackingFPS = DataModel(30L)

    @Expose
    @NumberParameter("Max Receive Timeout", "ms")
    var maxReceiveTimeout = DataModel(300L)

    @Expose
    @SliderParameter("Max Delta", 1.0, 100.0, 1.0, snap = true)
    var maxDelta = DataModel(50f)

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