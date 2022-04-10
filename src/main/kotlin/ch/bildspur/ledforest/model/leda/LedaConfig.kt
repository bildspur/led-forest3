package ch.bildspur.ledforest.model.leda

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.ParameterInformation
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class LedaConfig {
    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @Expose
    var landmarkColliders = mutableListOf<LandmarkPulseCollider>()

    @Expose
    @ParameterInformation("Minimal score a landmark needs to be valid.")
    @SliderParameter("Landmark Min Score", 0.0, 1.0, 0.01, snap = true)
    var landmarkMinScore = DataModel(0.1f)
}