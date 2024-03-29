package ch.bildspur.ledforest

import ch.bildspur.event.Event
import ch.bildspur.ledforest.artnet.ArtNetClient
import ch.bildspur.ledforest.controller.OscController
import ch.bildspur.ledforest.controller.PeasyController
import ch.bildspur.ledforest.controller.RemoteController
import ch.bildspur.ledforest.controller.midi.MidiController
import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.realsense.RealSenseDataProvider
import ch.bildspur.ledforest.scene.SceneManager
import ch.bildspur.ledforest.util.*
import ch.bildspur.ledforest.view.ArtNetRenderer
import ch.bildspur.ledforest.view.IRenderer
import ch.bildspur.ledforest.view.SceneRenderer
import ch.bildspur.ledforest.view.SoundRenderer
import ch.bildspur.model.DataModel
import ch.bildspur.postfx.builder.PostFX
import ch.bildspur.timer.ElapsedTimer
import ch.bildspur.util.round
import ddf.minim.Minim
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.opengl.PJOGL
import kotlin.math.roundToInt


/**
 * Created by cansik on 04.02.17.
 */
class Sketch : PApplet() {
    companion object {
        @JvmStatic
        val HIGH_RES_FRAME_RATE = 60f

        @JvmStatic
        val LOW_RES_FRAME_RATE = 30f

        @JvmStatic
        val WINDOW_WIDTH = 1024

        @JvmStatic
        val WINDOW_HEIGHT = 768

        @JvmStatic
        val CURSOR_HIDING_TIME = 1000L * 5L

        @JvmStatic
        val LOGBOOK_UPDATE_TIME = 1000L * 60L * 10L

        @JvmStatic
        val NAME = "LED Forest 3"

        @JvmStatic
        val VERSION = "3.0.0"

        @JvmStatic
        val URI_NAME = "ledforest"

        @JvmStatic
        lateinit var instance: Sketch

        @JvmStatic
        val isInstanceInitialized: Boolean
            get() = ::instance.isInitialized

        @JvmStatic
        fun map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double {
            return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
        }

        @JvmStatic
        fun map(value: Int, start1: Int, stop1: Int, start2: Int, stop2: Int): Int {
            return map(
                value.toDouble(),
                start1.toDouble(),
                stop1.toDouble(),
                start2.toDouble(),
                stop2.toDouble()
            ).roundToInt()
        }
    }


    @Volatile
    var isInitialised = false

    var onSetupFinished = Event<Sketch>()

    var onInitialisationFinished = Event<Sketch>()

    var fpsOverTime = 0f

    var isStatusViewShown = false

    @Volatile
    var isResetRendererProposed = false

    @Volatile
    var running = true

    private var is3D = true

    var isRendering = DataModel(true)

    var isInteractionOn = DataModel(true)

    val peasy = PeasyController(this)

    val osc = OscController(this)

    val timer = Timer()

    val remote = RemoteController(this)

    val project = DataModel(Project())

    val leapMotion = LeapDataProvider(this.project)

    val realSense = RealSenseDataProvider(this, this.project)

    val pose = PoseDataProvider(this, this.project)

    val artnet = ArtNetClient()

    val midi = MidiController(project)

    lateinit var canvas: PGraphics

    var lastCursorMoveTime = 0

    val renderer = mutableListOf<IRenderer>()

    val minim = Minim(this)

    lateinit var fx: PostFX

    val fpsTracker = FPSTracker()

    init {
    }

    override fun settings() {
        if (project.value.visualisation.isFullScreenMode.value) {
            fullScreen(PConstants.P3D, project.value.visualisation.fullScreenDisplay.value)
        } else {
            if (project.value.visualisation.disablePreview.value) {
                size(100, 100, PConstants.JAVA2D)
                is3D = false
            } else {
                size(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)
            }
        }

        PJOGL.profile = 1
        smooth()

        // retina screen
        if (project.value.visualisation.highResMode.value)
            pixelDensity = 2
    }

    override fun setup() {
        Sketch.instance = this
        surface.setResizable(true)

        if (is3D) {
            frameRate(if (project.value.visualisation.highFPSMode.value) HIGH_RES_FRAME_RATE else LOW_RES_FRAME_RATE)
        } else {
            surface.setVisible(false)
            frameRate(40f)
        }

        colorMode(HSB, 360f, 100f, 100f)

        project.onChanged += {
            onProjectChanged()
        }
        project.fire()

        if (is3D) {
            fx = PostFX(this)
            peasy.setup()
        }
        artnet.open()

        project.value.interaction.isLeapInteractionEnabled.onChanged += {
            if (it)
                leapMotion.start()
            else
                leapMotion.stop()
        }
        project.value.interaction.isLeapInteractionEnabled.fireLatest()

        project.value.interaction.isRealSenseInteractionEnabled.onChanged += {
            if (it)
                realSense.start()
            else
                realSense.stop()
        }
        project.value.interaction.isRealSenseInteractionEnabled.fireLatest()

        project.value.interaction.isPoseInteractionEnabled.onChanged += {
            if (it)
                pose.start()
            else
                pose.stop()
        }
        project.value.interaction.isPoseInteractionEnabled.fireLatest()

        // timer for cursor hiding
        timer.addTask(TimerTask(CURSOR_HIDING_TIME, {
            val current = millis()
            if (current - lastCursorMoveTime > CURSOR_HIDING_TIME)
                noCursor()
        }, "CursorHide"))

        // timer for logbook
        timer.addTask(TimerTask(LOGBOOK_UPDATE_TIME, {
            LogBook.log("Update", leapMotion.handCount, pose.totalPoseCount.getAndSet(0))
            leapMotion.handCount = 0
        }))

        LogBook.log("Start")
        onSetupFinished(this)
    }

    override fun draw() {
        background(0)

        if (skipFirstFrames()) {
            return
        }

        // setup long loading controllers
        if (initControllers())
            return

        // reset renderer if needed
        if (isResetRendererProposed)
            resetRenderer()

        // update tubes
        updateLEDColors()

        // first frame is draw
        if (!is3D) {
            println("starting renderer...")
            surface.setVisible(false)
            kotlin.concurrent.thread(isDaemon = true, block = { this.backgroundRenderer() })
            noLoop()
            return
        }

        canvas.draw {
            it.background(project.value.visualisation.clearColorBrightness.value.toInt())

            // render (update timer)
            if (isRendering.value)
                timer.update()

            peasy.applyTo(canvas)
        }

        // add hud
        peasy.hud {
            // output image
            if (project.value.visualisation.highResMode.value)
                fx.render(canvas)
                    .bloom(project.value.visualisation.bloomBrightPassThreshold.value.toFloat(), 20, 40f)
                    .compose()
            else
                image(canvas, 0f, 0f)

            showDebugInformation()
            drawSketchInformation(g)
        }
    }

    fun backgroundRenderer() {
        val limiter = FPSLimiter(project.value.targetFPS.value)
        project.value.targetFPS.onChanged += {
            limiter.targetFPS = it
        }
        project.value.targetFPS.fireLatest()

        val reportTimer = ElapsedTimer(100)

        while (running) {
            // reset renderer if needed
            if (isResetRendererProposed)
                resetRenderer()

            updateLEDColors()
            timer.update()

            // limiter.limit()

            val sleepTime = (1000.0 / project.value.targetFPS.value).toLong()
            Thread.sleep(sleepTime)

            fpsTracker.update()

            if (reportTimer.elapsed()) {
                project.value.currentFPS.value = "${fpsTracker.fps.round(2)}"
                project.value.currentLatency.value = "${fpsTracker.latency}"
            }
        }
    }

    fun showDebugInformation() {
        // render realsense information
        if (project.value.realSenseInteraction.isDebug.value) {
            realSense.renderDebug(g)
        }

        if (project.value.poseInteraction.isDebug.value) {
            pose.renderDebug(g)
        }
    }

    fun onProjectChanged() {
        surface.setTitle("$NAME ($VERSION) - ${project.value.name.value}")

        // setup hooks
        setupHooks()
    }

    fun setupHooks() {
        project.value.nodes.forEach {
            it.address.onChanged.clear()
            it.address.onChanged += {
                proposeResetRenderer()
            }
        }

        project.value.solidLEDColor.onChanged += {
            project.value.isSceneManagerEnabled.value = false
            project.value.lightElements.forEachLED {
                val hsv = project.value.solidLEDColor.value.toHSV()
                it.color.fade(ColorMode.color(hsv.h, hsv.s, hsv.v), 0.1f)
            }
        }
    }

    fun updateLEDColors() {
        project.value.lightElements.forEach { t ->
            t.leds.forEach { l ->
                l.color.update()
            }
        }
    }

    fun proposeResetRenderer() {
        if (isInitialised) {
            isResetRendererProposed = true
        }
    }

    fun resetRenderer() {
        println("resetting renderer...")

        renderer.forEach {
            timer.taskList.remove(it.timerTask)
            it.dispose()
        }

        renderer.clear()

        // add renderer
        if (is3D) {
            renderer.add(SceneRenderer(canvas, project.value.tubes, leapMotion, realSense, pose, project.value))
        }
        renderer.add(ArtNetRenderer(project.value, artnet, project.value.nodes))
        renderer.add(SceneManager(this, project.value, project.value.tubes))

        // make audio optional
        if (project.value.audio.soundEnabled.value)
            renderer.add(SoundRenderer(project.value, minim, leapMotion, project.value.tubes))

        isResetRendererProposed = false

        // rebuild
        // setup renderer
        renderer.forEach {
            it.setup()
            timer.addTask(it.timerTask)
        }
    }

    fun resetCanvas() {
        canvas = createGraphics(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)

        // retina screen
        if (project.value.visualisation.highResMode.value)
            canvas.pixelDensity = 2

        // view angle
        peasy.perspective()
    }

    fun skipFirstFrames(): Boolean {
        // skip first two frames
        if (frameCount < 2) {
            if (is3D) {
                peasy.hud {
                    textAlign(CENTER, CENTER)
                    fill(255)
                    textSize(20f)
                    text("${Sketch.NAME} is loading...", width / 2f, height / 2f)
                }
            }
            return true
        }

        return false
    }

    fun initControllers(): Boolean {
        if (!osc.isSetup) {
            osc.setup()

            if (is3D) {
                resetCanvas()
            }

            timer.setup()

            prepareExitHandler()

            // setting up renderer
            resetRenderer()

            isInitialised = true
            onInitialisationFinished(this)
            return true
        }

        return false
    }

    fun drawSketchInformation(pg: PGraphics) {
        // draw fps
        fpsOverTime += frameRate
        val averageFPS = fpsOverTime / frameCount.toFloat()

        pg.textAlign(LEFT, BOTTOM)
        pg.fill(255)
        pg.textSize(12f)
        pg.text(
            (if (project.value.visualisation.disableViewRendering.value) "View Disabled\n" else "") +
                    (if (project.value.poseInteraction.isDebug.value) "Pose Debug\n" else "") +
                    "FPS: ${frameRate.format(2)}\n" +
                    "FOT: ${averageFPS.format(2)}", 10f, height - 5f
        )
    }

    fun prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(Thread {
            println("shutting down...")
            running = false
            renderer.forEach { it.dispose() }
            leapMotion.stop()
            realSense.stop()
            osc.osc.stop()
            artnet.close()

            LogBook.log("Stop", leapMotion.handCount, pose.totalPoseCount.getAndSet(0))
        })
    }

    override fun keyPressed() {
        remote.processCommand(key)
    }

    override fun mouseMoved() {
        super.mouseMoved()
        cursor()
        lastCursorMoveTime = millis()
    }
}