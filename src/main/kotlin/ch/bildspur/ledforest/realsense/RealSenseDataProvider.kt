package ch.bildspur.ledforest.realsense

import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.realsense.tracking.ActiveRegion
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class RealSenseDataProvider(val project: DataModel<Project>) {
    private val updateTime = 15L

    var isRunning = false
        private set

    private lateinit var thread: Thread

    private val timer = Timer()

    private val activeRegionCache = ConcurrentHashMap<Int, ActiveRegion>()

    /**
     * Not thread safe -> Maybe using entry to make it concurrency safe
     */
    val activeRegions: MutableCollection<ActiveRegion>
        get() = activeRegionCache.values

    init {
        timer.addTask(TimerTask(updateTime, {
            readSensor()
        }))
    }

    fun start() {
        thread = thread {
            isRunning = true
            while (isRunning) {
                timer.update()
                Thread.sleep(updateTime)
            }
        }
    }

    fun stop() {
        isRunning = false
        thread.join(5000)
    }

    private fun readSensor() {
        /*
        if (!controller.isConnected)
            return

        if (controller.frame() == null)
            return
            */

        if (!project.value.isInteractionOn.value)
            return

        updateRegions()
    }

    private fun updateRegions() {

    }
}