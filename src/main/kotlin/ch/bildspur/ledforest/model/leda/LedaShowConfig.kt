package ch.bildspur.ledforest.model.leda

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.StringParameter
import kotlinx.datetime.Clock

class LedaShowConfig {

    @BooleanParameter("Show Requested")
    var triggerRequested = DataModel(false)

    @StringParameter("Video Name", isEditable = false)
    var videoName = DataModel("-")

    @StringParameter("Start Time", isEditable = false)
    private var startTimeStampPreview = DataModel("-")

    var startTimeStamp = DataModel(Clock.System.now())

    init {
        startTimeStamp.onChanged += {
            startTimeStampPreview.value = it.toString()
        }
    }
}