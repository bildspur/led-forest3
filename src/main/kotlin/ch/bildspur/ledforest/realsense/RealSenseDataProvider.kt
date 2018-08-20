package ch.bildspur.ledforest.realsense

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.realsense.io.RealSenseCamera
import ch.bildspur.ledforest.realsense.tracking.ActiveRegion
import ch.bildspur.ledforest.realsense.tracking.ActiveRegionTracker
import ch.bildspur.ledforest.realsense.util.*
import ch.bildspur.ledforest.realsense.vision.ActiveRegionDetector
import ch.bildspur.ledforest.realsense.vision.DepthImage
import ch.bildspur.ledforest.util.Synchronize
import org.opencv.core.Core
import org.opencv.core.Core.FONT_HERSHEY_SIMPLEX
import org.opencv.core.CvType.CV_8UC3
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector
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
        camera = RealSenseCamera(project.value.realSenseInteraction.inputWidth.value,
                project.value.realSenseInteraction.inputHeight.value,
                project.value.realSenseInteraction.inputFPS.value,
                enableDepthStream = true,
                enableColorStream = project.value.realSenseInteraction.enableColorStream.value)

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
        if (!project.value.interaction.isInteractionDataEnabled.value)
            return

        if (project.value.realSenseInteraction.isDebug.value && !isDebugger)
            return

        // set settings
        camera.depthLevelLow = project.value.realSenseInteraction.depthRange.value.lowValue.roundToInt()
        camera.depthLevelHigh = project.value.realSenseInteraction.depthRange.value.highValue.roundToInt()

        detector.threshold = project.value.realSenseInteraction.threshold.value
        detector.elementSize = project.value.realSenseInteraction.elementSize.value.roundToInt()
        detector.minAreaSize = project.value.realSenseInteraction.minAreaSize.value.roundToInt()

        tracker.sparsing = project.value.realSenseInteraction.sparsing.value
        tracker.maxDelta = project.value.realSenseInteraction.maxDelta.value

        val minLifeTime = project.value.realSenseInteraction.minLifeTime.value.roundToInt()

        // read streams
        camera.readStreams()

        // detect
        depthImage = DepthImage(camera.depthImage)
        detector.detect(depthImage)

        // track regions
        tracker.track(depthImage.components)

        // update normalizedPosition of regions (also very inaccurate depth)
        tracker.regions.forEach {
            val depthColor = camera.depthImage.get(it.x.roundToInt(), it.y.roundToInt()) and 0xFF
            val normalizedDepth = Sketch.map(depthColor.toDouble(), detector.threshold, 255.0, 0.0, 1.0)
            it.normalizedPosition.target = PVector(it.x.toFloat() / camera.width, it.y.toFloat() / camera.height, normalizedDepth.toFloat())
            it.normalizedPosition.easing = project.value.realSenseInteraction.activeRegionTranslationSpeed.value
            
            it.update()

            // map to interaction box todo: should not be here but is convenient
            it.mapToInteractionBox(project.value.interaction.interactionBox.value,
                    project.value.realSenseInteraction.flipX.value,
                    project.value.realSenseInteraction.flipY.value,
                    project.value.realSenseInteraction.flipZ.value)
        }

        // update regions synchronized
        activeRegions = tracker.regions.filter { it.lifeTime > minLifeTime }.toMutableList()

        project.value.realSenseInteraction.activeRegionCount.value = "${tracker.regions.size}"

        if (!isDebugger)
            depthImage.release()
    }

    fun renderDebug(g: PGraphics) {
        if (!isRunning)
            return

        readSensor(isDebugger = true)

        // convert grayscale to color image
        val debugImage = depthImage.gray.zeros(CV_8UC3)
        Imgproc.cvtColor(depthImage.gray, debugImage, Imgproc.COLOR_GRAY2BGR)

        // annotate
        activeRegions.forEachIndexed { i, it ->
            val position = Point(it.x, it.y)
            val color = Scalar(0.0, 255.0, 0.0)

            debugImage.drawMarker(position, color, markerSize = 50, thickness = 2)
            debugImage.drawText("A.$i (${it.normalizedPosition.z})", position.transform(20.0, 40.0), color,
                    thickness = 2,
                    scale = 1.0,
                    fontFace = FONT_HERSHEY_SIMPLEX)
        }

        // show debug pictures
        g.image(camera.depthImage, 0f, 0f, 512f, 384f)
        g.image(debugImage.toPImage(), 0f, 384f, 512f, 384f)

        // show color stream
        if (project.value.realSenseInteraction.displayColorStream.value)
            g.image(camera.colorImage, 0f, 0f, 512f, 384f)

        depthImage.release()
    }
}