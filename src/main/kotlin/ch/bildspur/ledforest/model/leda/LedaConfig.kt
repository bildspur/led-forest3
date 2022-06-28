package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.pose.KeyPoint
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.ledforest.web.BooleanWebEndpoint
import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import processing.core.PVector

class LedaConfig {
    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @Expose
    @BooleanParameter("Enable Collider")
    var enabledCollider = DataModel(true)

    @Expose
    @BooleanParameter("Display Collider")
    var displayCollider = DataModel(true)

    @Expose
    @BooleanParameter("Enable Interaction")
    @BooleanWebEndpoint("/interaction")
    var enabledInteraction = DataModel(true)

    @Expose
    @BooleanParameter("Enable Random Pulses")
    var enableRandomPulses = DataModel(false)

    @Expose
    @SliderParameter("Pulse Random Factor", 0.0, 1.0, 0.01)
    var pulseRandomFactor = DataModel(0.02f)

    @Expose
    @RangeSliderParameter("Gradient Spectrum", 0.0, 1.0, 0.01)
    var gradientSpectrum = DataModel(NumberRange(0.0, 0.25))

    @StringParameter("State", isEditable = false)
    var currentState = DataModel("-")

    @Expose
    var landmarkColliders = mutableListOf<LandmarkPulseCollider>()

    @Expose
    @ParameterInformation("Minimal score a landmark needs to be valid.")
    @SliderParameter("Landmark Min Score", 0.0, 1.0, 0.01, snap = true)
    var landmarkMinScore = DataModel(0.1f)

    @Expose
    @NumberParameter("Interactor Limit")
    var interactorLimit = DataModel(1)

    @Expose
    @BooleanParameter("Collider Scene Only")
    var colliderSceneOnly = DataModel(false)

    @Expose
    @PVectorParameter("Trigger Origin")
    var triggerOrigin = DataModel(PVector(0f, 2.8f, 0f))

    @ActionParameter("Trigger Origin", "Calibrate", invokesChange = false)
    private val calibrateTriggerOriginFromPose = {
        val maxTrys = 5

        val dataProvider = Sketch.instance.pose
        for (i in 0 until maxTrys) {
            if (dataProvider.poses.isNotEmpty()) {
                val pose = dataProvider.poses[0]

                // find ankle position
                val centerAnkle = KeyPoint.lerp(pose.leftAnkle, pose.rightAnkle, 0.5f)

                // add ground to feet height
                centerAnkle.z -= 0.08f

                triggerOrigin.value = centerAnkle
                break
            }

            Thread.sleep(200)
        }

        println("Could not find a valid pose for calibration.")
    }

    // todo: maybe add camera position relative to floor

    @GroupParameter("Collider")
    private var templateCollider = LandmarkPulseCollider()
}