package ch.bildspur.ledforest.model

import ch.bildspur.color.RGB
import ch.bildspur.ledforest.model.preset.PresetManager
import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.ColorParameter
import ch.bildspur.ui.properties.RangeSliderParameter
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class StarPatternConfig : PresetManager() {

    @Expose
    @SliderParameter("Rnd On Factor", 0.0, 1.0, 0.05, snap = true)
    var randomOnFactor = DataModel(0.95f)

    @Expose
    @SliderParameter("Rnd Off Factor", 0.0, 1.0, 0.05, snap = true)
    var randomOffFactor = DataModel(0.8f)

    @Expose
    @SliderParameter("Fade Speed", 0.001, 0.5, 0.001, snap = true)
    var fadeSpeed = DataModel(0.01f)

    @Expose
    @SliderParameter("Timer Interval", 0.0, 1000.0, 10.0, snap = true)
    var timerInterval = DataModel(500L)

    @Expose
    @BooleanParameter("Overwrite Color")
    var overwriteColor = DataModel(false)

    @Expose
    @ColorParameter("Color")
    var color = DataModel(RGB(1.0, 0.0, 0.0, 1.0))

    @Expose
    @RangeSliderParameter("Brightness Spectrum", 0.0, 100.0, 1.0, snap = true, roundInt = true)
    var brightnessSpectrum = DataModel(NumberRange(0.0, 100.0))
}