package ch.bildspur.ledforest.util

import java.util.*

object OSValidator {
    private val OS = System.getProperty("os.name").lowercase(Locale.getDefault())

    val isWindows: Boolean
        get() = OS.indexOf("win") >= 0

    val isMac: Boolean
        get() = OS.indexOf("mac") >= 0

    val isUnix: Boolean
        get() = OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0

    val isSolaris: Boolean
        get() = OS.indexOf("sunos") >= 0

    val isX86And64: Boolean
        get() = System.getProperty("os.arch") == "x86_64"

    val isArm64: Boolean
        get() = System.getProperty("os.arch") == "arm64"

    val isRosetta2: Boolean
        get() {
            if (!isMac) return false

            return try {
                val process = ProcessBuilder("sysctl", "-in", "sysctl.proc_translated").start()
                val result = process.inputStream.readBytes().decodeToString().trim()
                result == "1"
            } catch (ex: java.lang.Exception) {
                false
            }
        }
}