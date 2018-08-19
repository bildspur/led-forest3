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
    @SliderParameter("LED Width", 0.1, 10.0, 0.1)
    var ledWidth = DataModel(1f)

    @Expose
    @SliderParameter("LED Height", 0.1, 10.0, 0.1)
    var ledHeight = DataModel(2f)

    @Expose
    @SliderParameter("LED Detail", 2.0, 10.0, 1.0)
    var ledDetail = DataModel(5.0)
}