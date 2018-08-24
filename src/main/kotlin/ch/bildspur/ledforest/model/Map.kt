package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.bildspur.ledforest.ui.properties.ActionParameter
import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class Map {

    @Expose
    @BooleanParameter("Show Extended Name")
    var showExtendedName = DataModel(false)

    @Expose
    @SliderParameter("Map Scale Factor (m to px)", 1.0, 30.0, 1.0)
    var mapScaleFactor = DataModel(15.0f)

    @ActionParameter("Map", "Auto Scale")
    val autoScaleMap = {
        val scaleFactor = 1.2

        val ranges = Sketch.instance.spaceInformation.calculateTubeDimensions(Sketch.instance.project.value.tubes)
        val maxX = Math.max(Math.abs(ranges.x.lowValue), ranges.x.highValue)
        val maxY = Math.max(Math.abs(ranges.y.lowValue), ranges.y.highValue)

        val maxFactor = Math.max(TubeMap.CANVAS_WIDTH / 2.0 / maxX, TubeMap.CANVAS_HEIGHT / 2.0 / maxY)

        if (maxFactor > 0)
            mapScaleFactor.value = Math.floor(maxFactor / scaleFactor).toFloat()
    }
}