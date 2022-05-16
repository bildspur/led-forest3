package ch.bildspur.ledforest.model

import ch.bildspur.color.RGB
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.ColorParameter
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class TestConfig {
    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @Expose
    @SliderParameter("Interval", 1.0, 1000.0, 1.0, true)
    var interval = DataModel(100L)

    @Expose
    @SliderParameter("Size", 1.0, 100.0, 1.0, true)
    var size = DataModel(1)

    @Expose
    @SliderParameter("Fade", 0.01, 1.0, 0.01)
    var fade = DataModel(0.5f)

    @Expose
    @ColorParameter("Color")
    var color = DataModel(RGB(1.0, 0.0, 0.0, 1.0))

    @Expose
    @BooleanParameter("Solo Mode")
    var soloMode = DataModel(false)
}