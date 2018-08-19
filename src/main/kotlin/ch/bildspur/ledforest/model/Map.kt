package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.ui.properties.ActionParameter
import ch.bildspur.ledforest.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class Map {
    @Expose
    @SliderParameter("Map Scale Factor (m to px)", 1.0, 30.0, 5.0)
    var mapScaleFactor = DataModel(15.0f)

    @ActionParameter("Map", "Auto Scale")
    val autoScaleMap = {
        val scaleFactor = 2.4

        val ranges = Sketch.instance.spaceInformation.calculateTubeDimensions(Sketch.instance.project.value.tubes)

        /*
        interactionBox.value = PVector(
                Math.ceil(Math.max(Math.abs(ranges.x.lowValue), ranges.x.highValue) * scaleFactor).toFloat(),
                Math.ceil(Math.max(Math.abs(ranges.y.lowValue), ranges.y.highValue) * scaleFactor).toFloat(),
                Math.ceil(Math.max(Math.abs(ranges.z.lowValue), ranges.z.highValue) * scaleFactor).toFloat()
        )
        */
    }
}