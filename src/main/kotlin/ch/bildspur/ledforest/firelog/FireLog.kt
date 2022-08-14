package ch.bildspur.ledforest.firelog

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.httpPut
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

object FireLog {
    private const val EVENTS_COLLECTION = "events"
    private const val PINGS_COLLECTION = "pings"

    var enabled = false

    var enablePings = false
    var pingInterval = 60 * 1000L

    private var databaseUrl = ""
    private var secret = ""

    private var defaultApp = ""
    private var defaultView = ""
    private var defaultEventType = ""

    private val gson = Gson()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")

    val hasDatabase: Boolean
        get() = databaseUrl.isNotBlank()

    private var running = true

    init {
        thread(start = true, isDaemon = true) {
            while (running) {
                sendPing(FireLogPing(defaultApp, defaultView))
                Thread.sleep(pingInterval)
            }
        }
    }

    fun init(databaseUrl: String, secret: String) {
        this.databaseUrl = databaseUrl
        this.secret = secret
    }

    fun setDefaults(defaultApp: String = "", defaultView: String = "", defaultEventType: String = "") {
        FireLog.defaultApp = defaultApp
        FireLog.defaultView = defaultView
        FireLog.defaultEventType = defaultEventType
    }

    fun logEvent(event: FireLogEvent) {
        if (!enabled) return
        if (!hasDatabase) return

        val guid = UUID.randomUUID().toString().take(5)

        val dateTime = Date(event.timestamp)
        val tsText = dateFormat.format(dateTime)
        val eventId = "${tsText}-${guid}"

        val content = event.toJson()
        val json = gson.toJson(content)

        "$databaseUrl/$EVENTS_COLLECTION/${event.app}/${event.view}/$eventId.json?$authParameter"
            .httpPut()
            .jsonBody(json)
            .responseString { _, response, result ->
                if (response.isSuccessful)
                    return@responseString

                val errorMessage = result.get()
                System.err.println("Could not send event $errorMessage")
            }
    }

    fun log(
        app: String = defaultApp,
        view: String = defaultView,
        eventType: String = defaultEventType,
        timestamp: Long = System.currentTimeMillis(),
        params: Map<String, Any> = emptyMap()
    ) {
        logEvent(FireLogEvent(app, view, eventType, timestamp, params))
    }

    fun sendPing(ping: FireLogPing) {
        if (!enabled) return
        if (!hasDatabase) return

        val content = ping.toJson()
        val json = gson.toJson(content)

        "$databaseUrl/$PINGS_COLLECTION/${ping.app}/${ping.view}.json?$authParameter"
            .httpPut()
            .jsonBody(json)
            .responseString { _, response, result ->
                if (response.isSuccessful)
                    return@responseString

                val errorMessage = result.get()
                System.err.println("Could not send ping $errorMessage")
            }
    }

    private val authParameter: String
        get() {
            return secret.ifBlank {
                ""
            }
        }
}