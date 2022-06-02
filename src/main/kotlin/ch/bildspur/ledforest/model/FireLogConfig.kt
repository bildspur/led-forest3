package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.firelog.FireLog
import ch.bildspur.ledforest.ui.properties.SeparatorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose

class FireLogConfig {
    @Expose
    @BooleanParameter("Enabled", useToggleSwitch = true)
    var enabled = DataModel(false)

    @Expose
    @StringParameter("Database Url")
    var databaseUrl = DataModel("")

    @Expose
    @StringParameter("Secret")
    var secret = DataModel("")

    @SeparatorParameter("Defaults")
    private var defaultSep = Any()

    @Expose
    @StringParameter("App")
    var app = DataModel("LEDForest")

    @Expose
    @StringParameter("View")
    var view = DataModel("Leda")

    @Expose
    @StringParameter("Event Type")
    var eventType = DataModel("")

    @SeparatorParameter("Pings")
    private var pingsSep = Any()

    @Expose
    @BooleanParameter("Enable Pings", useToggleSwitch = true)
    var enablePing = DataModel(false)

    @Expose
    @NumberParameter("Ping Interval (ms)")
    var pingInterval = DataModel(1000 * 60L)

    @ActionParameter("FireLog", "Test Event", invokesChange = false)
    private val sendTestEvent = {
        FireLog.log(eventType = "test")
    }

    @ActionParameter("FireLog", "Reset", invokesChange = false)
    val updateFireLogInformation = {
        FireLog.init(databaseUrl.value, secret.value)
        FireLog.enabled = enabled.value
        FireLog.setDefaults(app.value, view.value, eventType.value)

        // pings
        FireLog.enablePings = enablePing.value
        FireLog.pingInterval = pingInterval.value
    }
}