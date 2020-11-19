package ch.bildspur.ledforest.model.interaction

import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.RangeSliderParameter
import ch.bildspur.ui.properties.SliderParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose

class LeapInteraction {

    @StringParameter("Hand Count", isEditable = false)
    var handCount = DataModel("-")

    @Expose
    @BooleanParameter("Single Color Int.")
    var singleColorInteraction = DataModel(false)

    @Expose
    @BooleanParameter("Enable Strobe")
    var isStrobeEnabled = DataModel(false)

    @Expose
    @SliderParameter("Strobe Threshold", 0.0, 1.0)
    var strobeThreshold = DataModel(0.8f)

    @Expose
    @SliderParameter("Interaction Distance", 0.1, 50.0, 0.1, snap = true)
    var interactionDistance = DataModel(5.0f)

    @Expose
    @RangeSliderParameter("Hue Spectrum", 0.0, 360.0, 1.0, snap = true, roundInt = true)
    var hueSpectrum = DataModel(NumberRange(180.0, 360.0))

    @Expose
    @SliderParameter("Hand Translation Speed", 0.01, 1.0, 0.01, snap = true)
    var handTranslationSpeed = DataModel(0.1f)

    @Expose
    @SliderParameter("Hand Rotation Speed", 0.01, 1.0, 0.01, snap = true)
    var handRotationSpeed = DataModel(0.5f)

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