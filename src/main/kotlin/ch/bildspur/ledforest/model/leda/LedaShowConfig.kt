package ch.bildspur.ledforest.model.leda

import ch.bildspur.color.RGB
import ch.bildspur.ledforest.ui.properties.SeparatorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.ColorParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max

class LedaShowConfig {

    @Expose
    @BooleanParameter("Show Enabled")
    var enabled = DataModel(false)

    @Expose
    @ColorParameter("Transition Color")
    var transitionColor = DataModel(RGB(255, 255, 255))

    @SeparatorParameter("Trigger")
    private val triggerSep = Any()

    @BooleanParameter("Show Requested")
    var showTrigger = DataModel(false)

    @StringParameter("Video Name", isEditable = false)
    var videoName = DataModel("-")

    @StringParameter("Triggered Time", isEditable = false)
    private var triggeredTimeStampPreview = DataModel("-")

    var triggeredTimeStamp = DataModel(Clock.System.now())

    @StringParameter("Start Time", isEditable = false)
    private var startTimeStampPreview = DataModel("-")

    var startTimeStamp = DataModel(Clock.System.now())

    @BooleanParameter("Show Ended")
    var hasShowEnded = DataModel(false)

    init {
        startTimeStamp.onChanged += {
            startTimeStampPreview.value = it.toLocalDateTime(TimeZone.currentSystemDefault())
                .toInstant(TimeZone.UTC).toString()
        }

        triggeredTimeStamp.onChanged += {
            triggeredTimeStampPreview.value = it.toLocalDateTime(TimeZone.currentSystemDefault())
                .toInstant(TimeZone.UTC).toString()
        }
    }

    val showRequested: Boolean
        get() = enabled.value && showTrigger.value

    val isShowRunning: Boolean
        get() = enabled.value && !hasShowEnded.value

    val timeUntilPlayback: Long
        get() = startTimeStamp.value.toEpochMilliseconds() - System.currentTimeMillis()

    val totalWaitTime: Long
        get() = max(0, startTimeStamp.value.toEpochMilliseconds() - triggeredTimeStamp.value.toEpochMilliseconds())
}