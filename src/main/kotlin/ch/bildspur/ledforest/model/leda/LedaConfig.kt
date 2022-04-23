package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import processing.core.PVector

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

    @Expose
    @NumberParameter("Interactor Limit")
    var interactorLimit = DataModel(1)

    @Expose
    @PVectorParameter("Trigger Origin")
    var triggerOrigin = DataModel(PVector(0f, 0f, 2.8f))

    @GroupParameter("Collider")
    private var templateCollider = LandmarkPulseCollider()
}