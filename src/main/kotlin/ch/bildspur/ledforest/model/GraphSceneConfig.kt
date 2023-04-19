package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.preset.PresetManager
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.NumberParameter
import com.google.gson.annotations.Expose

class GraphSceneConfig : PresetManager() {
    @Expose
    @NumberParameter("Max Node Distance")
    val maxNodeDistance = DataModel(0.5)
}