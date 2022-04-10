package ch.bildspur.ledforest.model.leda

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import com.google.gson.annotations.Expose

class LedaConfig {
    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @Expose
    var landmarkColliders = mutableListOf<LandmarkPulseCollider>()
}