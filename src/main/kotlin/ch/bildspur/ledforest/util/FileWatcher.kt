package ch.bildspur.ledforest.util

import ch.bildspur.event.Event
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import kotlin.concurrent.thread


class FileWatcher(private var filePath: Path = Paths.get(""), var interval: Long = 500) {
    val onChange = Event<Path>()

    val file: Path
        get() = filePath

    @Volatile
    private var running = true
    private var lastTimeStamp = -1L

    private val thread = thread(isDaemon = true, block = {
        while (running) {
            if (Files.exists(filePath)) {
                val attr = Files.readAttributes(filePath, BasicFileAttributes::class.java)
                val ts = attr.lastModifiedTime().toMillis()

                if (lastTimeStamp > 0 && ts != lastTimeStamp) {
                    onChange(filePath)
                }

                lastTimeStamp = ts
            }
            Thread.sleep(interval)
        }
    })

    fun reset(filePath: Path) {
        this.filePath = filePath
        lastTimeStamp = -1L
    }

    fun stop(wait: Boolean = false) {
        running = false

        if (wait)
            thread.join(5 * interval)
    }
}