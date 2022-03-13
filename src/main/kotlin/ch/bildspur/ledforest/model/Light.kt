package ch.bildspur.ledforest.model

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class Light {
    @Expose
    @BooleanParameter("ArtNet Rendering")
    var isArtNetRendering = DataModel(true)

    @Expose
    @SliderParameter("Luminosity", 0.0, 1.0, 0.01)
    var luminosity = DataModel(1f)

    @Expose
    @SliderParameter("Response", 0.0, 1.0, 0.01)
    var response = DataModel(0.5f)

    @Expose
    @SliderParameter("Trace", 0.0, 1.0, 0.01)
    var trace = DataModel(0f)
}