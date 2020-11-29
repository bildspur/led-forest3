package ch.bildspur.ledforest.model.interaction

import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.ui.properties.*
import ch.bildspur.util.Mapping
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
    @SliderParameter("Max Delta", 0.01, 1.0, 0.01, mapping = Mapping.Quad, labelDigits = 3)
    var maxDelta = DataModel(0.1f)

    @Expose
    @SliderParameter("Min Alive Time", 0.0, 1000.0, 1.0, snap = true)
    var minAliveTime = DataModel(400L)

    @Expose
    @SliderParameter("Max Dead Time", 0.0, 1000.0, 1.0, snap = true)
    var maxDeadTime = DataModel(400L)

    @Expose
    @SliderParameter("Position Easing", 0.01, 1.0, mapping = Mapping.Quad)
    var positionEasing = DataModel(0.2f)

    @LabelParameter("Interaction")

    // orientation
    @Expose
    @BooleanParameter("Flip X", useToggleSwitch = true)
    var flipX = DataModel(false)

    @Expose
    @BooleanParameter("Flip Y", useToggleSwitch = true)
    var flipY = DataModel(false)

    @Expose
    @BooleanParameter("Flip Z", useToggleSwitch = true)
    var flipZ = DataModel(false)

    @Expose
    @RangeSliderParameter("Interaction Distance", 0.01, 5.0, 0.01, snap = true)
    var interactionDistanceRange = DataModel(NumberRange(1.0, 2.0))

    @Expose
    @RangeSliderParameter("Hue Spectrum", 0.0, 360.0, 1.0, snap = true, roundInt = true)
    var hueSpectrum = DataModel(NumberRange(160.0, 320.0))

    @Expose
    @SliderParameter("Saturation", 0.0, 100.0, 1.0, snap = true, roundInt = true)
    var saturation = DataModel(100.0f)

    @Expose
    @SliderParameter("Brightness", 0.0, 100.0, 1.0, snap = true, roundInt = true)
    var brightness = DataModel(100.0f)

    @Expose
    @SliderParameter("LED Fading Speed", 0.1, 1.0, 0.1, snap = true)
    var fadingSpeed = DataModel(0.1f)
}