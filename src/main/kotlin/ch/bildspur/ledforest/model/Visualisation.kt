package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.IntParameter
import ch.bildspur.ledforest.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class Visualisation {
    @Expose
    @BooleanParameter("High Res Mode*")
    var highResMode = DataModel(true)

    @Expose
    @BooleanParameter("High FPS Mode*")
    var highFPSMode = DataModel(true)

    @Expose
    @BooleanParameter("High Detail Mode")
    var highDetail = DataModel(true)

    @Expose
    @BooleanParameter("Fullscreen Mode*")
    var isFullScreenMode = DataModel(false)

    @Expose
    @IntParameter("Fullscreen Display*")
    var fullScreenDisplay = DataModel(0)

    @Expose
    @SliderParameter("Clear Color Brightness", 0.0, 255.0, 1.0, roundInt = true)
    var clearColorBrightness = DataModel(12.0)

    @Expose
    @SliderParameter("Global Scale Factor (m to px)", 5.0, 200.0, 5.0)
    var globalScaleFactor = DataModel(50.0f)

    @Expose
    @SliderParameter("LED Width", 0.01, 0.1, 0.01)
    var ledWidth = DataModel(0.03f)

    @Expose
    @SliderParameter("LED Height", 0.01, 0.1, 0.01)
    var ledHeight = DataModel(0.06f)

    @Expose
    @SliderParameter("LED Detail", 2.0, 10.0, 1.0)
    var ledDetail = DataModel(5.0)

    @Expose
    @BooleanParameter("Display Floor")
    var displayFloor = DataModel(true)
}