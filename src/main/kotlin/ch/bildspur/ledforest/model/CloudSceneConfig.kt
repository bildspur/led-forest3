package ch.bildspur.ledforest.model

import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.LabelParameter
import ch.bildspur.ui.properties.RangeSliderParameter
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class CloudSceneConfig {

    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @LabelParameter("Speed")

    @Expose
    @SliderParameter("Timer Interval", 0.0, 1000.0, 10.0, snap = true)
    var timerInterval = DataModel(20L)

    @Expose
    @BooleanParameter("Enable Fading")
    var enableFading = DataModel(false)

    @Expose
    @SliderParameter("Fade Speed", 0.1, 1.0, 0.1, snap = true)
    var fadeSpeed = DataModel(0.5f)

    @LabelParameter("Noise")

    @Expose
    @SliderParameter("Noise Speed", 0.0001, 0.001, 0.0001, snap = true)
    var noiseSpeed = DataModel(0.0005f)

    @Expose
    @SliderParameter("LOD", 1.0, 16.0, 1.0, snap = true, roundInt = true)
    var lod = DataModel(8)

    @Expose
    @SliderParameter("Fall Off", 0.0, 1.0, 0.01, snap = true)
    var fallOff = DataModel(0.65f)

    @Expose
    @SliderParameter("Scale", 0.01, 4.0, 0.01, snap = true)
    var scale = DataModel(1.0f)

    @Expose
    @BooleanParameter("Mod X")
    var modX = DataModel(true)

    @Expose
    @BooleanParameter("Mod Y")
    var modY = DataModel(true)

    @Expose
    @BooleanParameter("Mod Z")
    var modZ = DataModel(false)

    @Expose
    @BooleanParameter("Mod Easing")
    var modEasing = DataModel(false)

    @LabelParameter("Color")

    @Expose
    @SliderParameter("Contrast", 0.0, 1.0, 0.1, snap = true)
    var contrast = DataModel(1.0f)

    @Expose
    @RangeSliderParameter("Hue Spectrum", 0.0, 360.0, 1.0, snap = true, roundInt = true)
    var hueSpectrum = DataModel(NumberRange(160.0, 320.0))

    @Expose
    @RangeSliderParameter("Saturation Spectrum", 0.0, 100.0, 1.0, snap = true, roundInt = true)
    var saturationSpectrum = DataModel(NumberRange(80.0, 100.0))

    @Expose
    @RangeSliderParameter("Brightness Spectrum", 0.0, 100.0, 1.0, snap = true, roundInt = true)
    var brightnessSpectrum = DataModel(NumberRange(0.0, 100.0))
}