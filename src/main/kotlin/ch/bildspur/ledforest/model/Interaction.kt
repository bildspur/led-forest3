package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class Interaction {
    @Expose
    @SliderParameter("Int. Distance", 0.0, 200.0)
    var interactionDistance = DataModel(75f)
}