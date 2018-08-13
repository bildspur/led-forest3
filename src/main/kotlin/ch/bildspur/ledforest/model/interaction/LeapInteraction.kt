package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.NumberRange
import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.RangeSliderParameter
import ch.bildspur.ledforest.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class LeapInteraction {
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
    @SliderParameter("Int. Distance", 1.0, 200.0, 1.0, snap = true, roundInt = true)
    var interactionDistance = DataModel(75f)

    @Expose
    @RangeSliderParameter("Hue Spectrum", 0.0, 360.0, 1.0, snap = true, roundInt = true)
    var hueSpectrum = DataModel(NumberRange(180.0, 360.0))
}