package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.NumberRange
import ch.bildspur.ledforest.ui.properties.ActionParameter
import ch.bildspur.ledforest.ui.properties.FloatParameter
import ch.bildspur.ledforest.ui.properties.RangeSliderParameter
import com.google.gson.annotations.Expose

class RealSenseInteraction {

    @ActionParameter("Camera", "Restart")
    val restartCamera = {
        val rs = Sketch.instance.realSense
        rs.stop()
        rs.start()
    }

    @ActionParameter("Camera", "Show Range")
    val showRange = {
        println("Range: $depthRange")
    }

    @Expose
    @FloatParameter("Input Width")
    var inputWidth = DataModel(640)

    @Expose
    @FloatParameter("Input Height")
    var inputHeight = DataModel(480)

    @Expose
    @FloatParameter("Input FPS")
    var inputFPS = DataModel(30)

    @Expose
    @RangeSliderParameter("Depth Range", 0.0, 65536.0, 1.0, snap = true, roundInt = true)
    var depthRange = DataModel(NumberRange(0.0, 4096.0))
}