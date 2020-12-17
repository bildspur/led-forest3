package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.annotations.Expose
import kotlin.math.abs
import kotlin.math.floor

class Map {

    @Expose
    @BooleanParameter("Show Extended Name")
    var showExtendedName = DataModel(false)

    @Expose
    @SliderParameter("Map Scale Factor (m to px)", 1.0, 100.0, 1.0)
    var mapScaleFactor = DataModel(15.0f)

    @ActionParameter("Map", "Auto Scale")
    val autoScaleMap = {
        val scaleFactor = 0.8

        val ranges = Sketch.instance.spaceInformation.calculateTubeDimensions(Sketch.instance.project.value.tubes)
        val maxX = abs(ranges.x.low).coerceAtLeast(ranges.x.high)
        val maxY = abs(ranges.y.low).coerceAtLeast(ranges.y.high)


        val ratio = if(maxX >= maxY) {
            TubeMap.CANVAS_WIDTH / (2.0 * maxX)
        } else {
            TubeMap.CANVAS_HEIGHT / (2.0 * maxY)
        }

        if (ratio > 0)
            mapScaleFactor.value = floor(ratio * scaleFactor).toFloat()
    }
}