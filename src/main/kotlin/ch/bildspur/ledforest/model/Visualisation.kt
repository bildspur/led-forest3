package ch.bildspur.ledforest.model

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.SliderParameter
import com.google.gson.annotations.Expose

class Visualisation {
    @Expose
    @BooleanParameter("Disable Preview*")
    var disablePreview = DataModel(false)

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
    @NumberParameter("Fullscreen Display*")
    var fullScreenDisplay = DataModel(0)

    @Expose
    @BooleanParameter("Debug Information")
    var displayDebugInformation = DataModel(true)

    @Expose
    @BooleanParameter("Cage Tubes")
    var displayCubeCage = DataModel(false)

    @Expose
    @BooleanParameter("Disable 3D-Rendering for Production")
    var disableViewRendering = DataModel(false)

    @Expose
    @SliderParameter("Bloom BrightPass Threshold", 0.0, 1.0, 0.05)
    var bloomBrightPassThreshold = DataModel(0.15)

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
    @SliderParameter("LED Detail", 2.0, 10.0)
    var ledDetail = DataModel(5)

    @Expose
    @BooleanParameter("Display Floor")
    var displayFloor = DataModel(false)

    @Expose
    @SliderParameter("Floor Z-Height", 0.1, 10.0, 0.1)
    var floorZHeight = DataModel(0.2f)
}