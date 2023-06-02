package ch.bildspur.ledforest.model.leda

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import kotlinx.datetime.Clock

class LedaShowConfig {

    @Expose
    @BooleanParameter("Show Enabled")
    var enabled = DataModel(false)

    @BooleanParameter("Show Requested")
    var showTrigger = DataModel(false)

    @StringParameter("Video Name", isEditable = false)
    var videoName = DataModel("-")

    @StringParameter("Start Time", isEditable = false)
    private var startTimeStampPreview = DataModel("-")

    var startTimeStamp = DataModel(Clock.System.now())

    @BooleanParameter("Show Ended")
    var hasShowEnded = DataModel(false)

    init {
        startTimeStamp.onChanged += {
            startTimeStampPreview.value = it.toString()
        }
    }

    val showRequested: Boolean
        get() = enabled.value && showTrigger.value

    val isShowRunning: Boolean
        get() = enabled.value && !hasShowEnded.value
}