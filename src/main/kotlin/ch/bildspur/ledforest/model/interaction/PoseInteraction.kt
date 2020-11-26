package ch.bildspur.ledforest.model.interaction

import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.ui.properties.*
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

    @LabelParameter("Tracking")

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

    @Expose
    @SliderParameter("Min Alive Time", 0.0, 1000.0, 1.0, snap = true)
    var minAliveTime = DataModel(400L)

    @Expose
    @SliderParameter("Max Dead Time", 0.0, 1000.0, 1.0, snap = true)
    var maxDeadTime = DataModel(400L)

    @Expose
    @SliderParameter("Position Easing", 0.01, 1.0, 0.01, snap = true)
    var positionEasing = DataModel(0.2f)

    @LabelParameter("Interaction")

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

    @Expose
    @SliderParameter("Interaction Distance", 0.01, 5.0, 0.01, snap = true)
    var interactionDistance = DataModel(1.0f)

    @Expose
    @RangeSliderParameter("Hue Spectrum", 0.0, 360.0, 1.0, snap = true, roundInt = true)
    var hueSpectrum = DataModel(NumberRange(160.0, 320.0))
}