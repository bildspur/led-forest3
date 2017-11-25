package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.configuration.ConfigurationController
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LogBook {
    private var separator: String = ";"
    private val logFileDirectory = ConfigurationController.CONFIGURATION_DIR
    private var logFileName = "ledforest.log"

    fun log(message: String, vararg attributes: Any = emptyArray()) {
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val dateTime = current.format(formatter)

        val entries = listOf(dateTime, message, *attributes.map { it.toString() }.toTypedArray())
        val logEntry = entries.joinToString(separator)

        File(Paths.get(logFileDirectory.toString(), logFileName).toUri()).appendText("$logEntry\n")
    }
}