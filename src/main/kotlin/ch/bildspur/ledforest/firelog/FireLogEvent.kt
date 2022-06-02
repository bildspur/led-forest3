package ch.bildspur.ledforest.firelog

import com.google.gson.annotations.Expose
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

data class FireLogEvent(
    @Expose
    val app: String,
    @Expose
    val view: String,
    @Expose
    val eventType: String,
    @Expose
    val timestamp: Long = System.currentTimeMillis(),
    @Expose
    val params: Map<String, Any> = emptyMap()
) {
    fun toJson(): Map<String, Any> {
        return buildMap() {
            put(
                "ts", Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            put("type", eventType)
            put("params", params)
        }
    }
}