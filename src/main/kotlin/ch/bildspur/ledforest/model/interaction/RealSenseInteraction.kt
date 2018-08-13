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
    @BooleanParameter("Display Color Stream")
    var displayColorStream = DataModel(false)

    @Expose
    @BooleanParameter("Enable Color Sream")
    var enableColorStream = DataModel(true)

    @Expose
    @StringParameter("Active Region Count", isEditable = false)
    var activeRegionCount = DataModel("-")

    @Expose
    @IntParameter("Input Width")
    var inputWidth = DataModel(640)

    @Expose
    @IntParameter("Input Height")
    var inputHeight = DataModel(480)

    @Expose
    @IntParameter("Input FPS")
    var inputFPS = DataModel(30)

    @Expose
    @RangeSliderParameter("Depth Range", 0.0, 65536.0, 1.0, snap = true, roundInt = true)
    var depthRange = DataModel(NumberRange(0.0, 4096.0))

    @Expose
    @SliderParameter("Binary Threshold", 0.0, 255.0, 1.0, snap = true, roundInt = true)
    var threshold = DataModel(200.0)

    @Expose
    @SliderParameter("MT Element Size", 1.0, 10.0, 1.0, snap = true, roundInt = true)
    var elementSize = DataModel(5.0)

    @Expose
    @SliderParameter("Min Area Size", 1.0, 500.0, 1.0, snap = true, roundInt = true)
    var minAreaSize = DataModel(125.0)

    @Expose
    @SliderParameter("Area Sparsing", 0.0, 500.0, 1.0, snap = true, roundInt = true)
    var sparsing = DataModel(0.0)

    @Expose
    @SliderParameter("Max Delta", 0.0, 500.0, 1.0, snap = true, roundInt = true)
    var maxDelta = DataModel(100.0)

    @Expose
    @SliderParameter("Min Lifetime", 0.0, 500.0, 1.0, snap = true, roundInt = true)
    var minLifeTime = DataModel(20.0)

}