package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.NumberRange
import ch.bildspur.ledforest.ui.properties.*
import com.google.gson.annotations.Expose

class RealSenseInteraction {

    @ActionParameter("Camera", "Restart")
    val restartCamera = {
        val rs = Sketch.instance.realSense
        rs.stop()
        rs.start()
    }

    @Expose
    @BooleanParameter("Debug")
    var isDebug = DataModel(false)

    @Expose
    @StringParameter("Active Region Count", isEditable = false)
    var activeRegionCount = DataModel("-")

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