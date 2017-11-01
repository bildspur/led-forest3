package ch.bildspur.ledforest.util

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LogBook {
    private var separator: String = ";"
    private var logFileName = "logbook.txt"

    fun log(message: String, vararg attributes: Any = emptyArray()) {
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val dateTime = current.format(formatter)

        val entries = listOf(dateTime, message, *attributes.map { it.toString() }.toTypedArray())
        val logEntry = entries.joinToString(separator)
        File(logFileName).appendText("$logEntry\n")
    }
}