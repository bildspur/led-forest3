package ch.bildspur.ledforest.model

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.NumberParameter
import com.google.gson.annotations.Expose

class GraphSceneConfig {
    @Expose
    @NumberParameter("Max Node Distance")
    val maxNodeDistance = DataModel(0.5)
}