package ch.bildspur.ledforest.model

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class Audio {
    @Expose
    @BooleanParameter("Audio Enabled*")
    var soundEnabled = DataModel(true)

    @Expose
    @SliderParameter("Background Gain", -35.0, 10.0, 1.0, snap = true, roundInt = true)
    val backgroundGain = DataModel(0.0)

    @Expose
    @SliderParameter("Rattle Gain", -35.0, 10.0, 1.0, snap = true, roundInt = true)
    val rattleGain = DataModel(0.0)
}