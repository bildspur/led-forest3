package ch.bildspur.ledforest.realsense

import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.realsense.io.RealSenseCamera
import ch.bildspur.ledforest.realsense.tracking.ActiveRegion
import ch.bildspur.ledforest.realsense.tracking.ActiveRegionTracker
import ch.bildspur.ledforest.realsense.util.toPImage
import ch.bildspur.ledforest.realsense.vision.ActiveRegionDetector
import ch.bildspur.ledforest.realsense.vision.DepthImage
import ch.bildspur.ledforest.util.Synchronize
import org.opencv.core.Core
import processing.core.PApplet
import processing.core.PGraphics
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

    private lateinit var depthImage: DepthImage

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

    private fun readSensor(isDebugger: Boolean = false) {
        if (!project.value.interaction.isInteractionOn.value)
            return

        if (project.value.realSenseInteraction.isDebug.value && !isDebugger)
            return

        // set settings
        camera.depthLevelLow = project.value.realSenseInteraction.depthRange.value.lowValue.roundToInt()
        camera.depthLevelHigh = project.value.realSenseInteraction.depthRange.value.highValue.roundToInt()

        detector.threshold = project.value.realSenseInteraction.threshold.value
        detector.elementSize = project.value.realSenseInteraction.elementSize.value.roundToInt()
        detector.minAreaSize = project.value.realSenseInteraction.minAreaSize.value.roundToInt()

        // read streams
        camera.readStreams()

        // detect
        depthImage = DepthImage(camera.depthImage)
        detector.detect(depthImage)

        // track regions
        tracker.track(depthImage.components)

        // update regions synchronized
        activeRegions = tracker.regions.toMutableList()

        project.value.realSenseInteraction.activeRegionCount.value = "${tracker.regions.size}"

        if (!isDebugger)
            depthImage.release()
    }

    fun renderDebug(g: PGraphics) {
        readSensor(isDebugger = true)

        g.image(camera.depthImage, 0f, 0f, 320f, 240f)
        g.image(depthImage.gray.toPImage(), 0f, 240f, 320f, 240f)

        depthImage.release()
    }
}