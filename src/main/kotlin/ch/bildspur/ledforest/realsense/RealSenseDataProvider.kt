package ch.bildspur.ledforest.realsense

import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.realsense.io.RealSenseCamera
import ch.bildspur.ledforest.realsense.tracking.ActiveRegion
import ch.bildspur.ledforest.realsense.tracking.ActiveRegionTracker
import ch.bildspur.ledforest.realsense.vision.ActiveRegionDetector
import ch.bildspur.ledforest.realsense.vision.DepthImage
import ch.bildspur.ledforest.util.Synchronize
import org.opencv.core.Core
import processing.core.PApplet
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class RealSenseDataProvider(val sketch: PApplet, val project: DataModel<Project>) {
    private val updateTime = 15L

    var isRunning = false
        private set

    private lateinit var thread: Thread

    private val timer = Timer()

    private lateinit var detector: ActiveRegionDetector

    private lateinit var tracker: ActiveRegionTracker

    private lateinit var camera: RealSenseCamera

    var activeRegions by Synchronize(mutableListOf<ActiveRegion>())

    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        timer.addTask(TimerTask(updateTime, {
            readSensor()
        }))
    }

    fun start() {
        detector = ActiveRegionDetector()
        tracker = ActiveRegionTracker()
        camera = RealSenseCamera(sketch,
                project.value.realSenseInteraction.inputWidth.value,
                project.value.realSenseInteraction.inputHeight.value,
                project.value.realSenseInteraction.inputFPS.value)

        thread = thread {
            isRunning = true

            try {
                camera.setup()
            } catch (ex: Exception) {
                isRunning = false
                println("Could not start real sense camera! (${ex.message})")
            }

            while (isRunning) {
                timer.update()
                Thread.sleep(updateTime)
            }
        }
    }

    fun stop() {
        isRunning = false

        if (::thread.isInitialized)
            thread.join(5000)
    }

    private fun readSensor() {
        if (!project.value.interaction.isInteractionOn.value)
            return

        // set settings and read streams
        camera.depthLevelLow = project.value.realSenseInteraction.depthRange.value.lowValue.roundToInt()
        camera.depthLevelHigh = project.value.realSenseInteraction.depthRange.value.highValue.roundToInt()

        camera.readStreams()

        // detect
        val image = DepthImage(camera.depthImage)
        detector.detect(image)

        // track regions
        tracker.track(image.components)

        // update regions synchronized
        activeRegions = tracker.regions.toMutableList()

        println("Current Active Regions: ${tracker.regions.size}")
    }
}