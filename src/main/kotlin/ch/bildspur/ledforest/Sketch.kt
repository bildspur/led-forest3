package ch.bildspur.ledforest

import ch.bildspur.ledforest.artnet.ArtNetClient
import ch.bildspur.ledforest.controller.OscController
import ch.bildspur.ledforest.controller.PeasyController
import ch.bildspur.ledforest.controller.RemoteController
import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.realsense.RealSenseDataProvider
import ch.bildspur.ledforest.scene.SceneManager
import ch.bildspur.ledforest.util.LogBook
import ch.bildspur.ledforest.util.draw
import ch.bildspur.ledforest.util.format
import ch.bildspur.ledforest.view.ArtNetRenderer
import ch.bildspur.ledforest.view.IRenderer
import ch.bildspur.ledforest.view.SceneRenderer
import ch.bildspur.ledforest.view.SoundRenderer
import ch.bildspur.postfx.builder.PostFX
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
        val WINDOW_WIDTH = 768
        @JvmStatic
        val WINDOW_HEIGHT = 576

        @JvmStatic
        val CURSOR_HIDING_TIME = 1000L * 5L

        @JvmStatic
        val LOGBOOK_UPDATE_TIME = 1000L * 60L * 10L

        @JvmStatic
        val NAME = "LED Forest 3"

        @JvmStatic
        val VERSION = "0.5"

        @JvmStatic
        val URI_NAME = "ledforest"

        @JvmStatic
        lateinit var instance: Sketch

        @JvmStatic
        fun map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double {
            return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
        }

        @JvmStatic
        fun map(value: Int, start1: Int, stop1: Int, start2: Int, stop2: Int): Int {
            return map(value.toDouble(), start1.toDouble(), stop1.toDouble(), start2.toDouble(), stop2.toDouble()).roundToInt()
        }
    }


    @Volatile
    var isInitialised = false

    var fpsOverTime = 0f

    var isStatusViewShown = false

    @Volatile
    var isResetRendererProposed = false

    var isRendering = DataModel(true)

    var isInteractionOn = DataModel(true)

    val peasy = PeasyController(this)

    val osc = OscController(this)

    val timer = Timer()

    val remote = RemoteController(this)

    val project = DataModel(Project())

    val leapMotion = LeapDataProvider(this.project)

    val realSense = RealSenseDataProvider(this, this.project)

    val artnet = ArtNetClient()

    lateinit var canvas: PGraphics

    var lastCursorMoveTime = 0

    val renderer = mutableListOf<IRenderer>()

    val minim = Minim(this)

    lateinit var fx: PostFX

    init {
    }

    override fun settings() {
        if (project.value.isFullScreenMode.value)
            fullScreen(PConstants.P3D, project.value.fullScreenDisplay.value)
        else
            size(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)

        PJOGL.profile = 1
        smooth()

        // retina screen
        if (project.value.highResMode.value)
            pixelDensity = 2
    }

    override fun setup() {
        Sketch.instance = this

        frameRate(if (project.value.highFPSMode.value) HIGH_RES_FRAME_RATE else LOW_RES_FRAME_RATE)
        colorMode(HSB, 360f, 100f, 100f)

        project.onChanged += {
            onProjectChanged()
        }
        project.fire()

        fx = PostFX(this)
        peasy.setup()
        artnet.open()

        leapMotion.start()
        realSense.start()

        // timer for cursor hiding
        timer.addTask(TimerTask(CURSOR_HIDING_TIME, {
            val current = millis()
            if (current - lastCursorMoveTime > CURSOR_HIDING_TIME)
                noCursor()
        }, "CursorHide"))

        // timer for logbook
        timer.addTask(TimerTask(LOGBOOK_UPDATE_TIME, {
            LogBook.log("Update", leapMotion.handCount)
            leapMotion.handCount = 0
        }))

        LogBook.log("Start")
    }

    override fun draw() {
        background(0)

        if (skipFirstFrames())
            return

        // setup long loading controllers
        if (initControllers())
            return

        // reset renderer if needed
        if (isResetRendererProposed)
            resetRenderer()

        // update tubes
        updateLEDColors()

        canvas.draw {
            it.background(0)

            // render (update timer)
            if (isRendering.value)
                timer.update()

            peasy.applyTo(canvas)
        }

        // add hud
        peasy.hud {
            // output image
            if (project.value.highResMode.value)
                fx.render(canvas)
                        .bloom(0.0f, 20, 40f)
                        .compose()
            else
                image(canvas, 0f, 0f)

            showDebugInformation()
            drawFPS(g)
        }
    }

    fun showDebugInformation() {
        // render realsense information
        if (project.value.realSenseInteraction.isDebug.value)
            Sketch.instance.realSense.renderDebug(g)
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
    }

    fun updateLEDColors() {
        project.value.tubes.forEach { t ->
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
        println("reseting renderer...")

        renderer.forEach {
            timer.taskList.remove(it.timerTask)
            it.dispose()
        }

        renderer.clear()

        // add renderer
        renderer.add(SceneRenderer(canvas, project.value.tubes, leapMotion, project.value))
        renderer.add(ArtNetRenderer(project.value, artnet, project.value.nodes, project.value.tubes))
        renderer.add(SceneManager(project.value, project.value.tubes))

        // make sound optional
        if (project.value.isSound.value)
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
        if (project.value.highResMode.value)
            canvas.pixelDensity = 2
    }

    fun skipFirstFrames(): Boolean {
        // skip first two frames
        if (frameCount < 2) {
            peasy.hud {
                textAlign(CENTER, CENTER)
                fill(255)
                textSize(20f)
                text("${Sketch.NAME} is loading...", width / 2f, height / 2f)
            }
            return true
        }

        return false
    }

    fun initControllers(): Boolean {
        if (!osc.isSetup) {
            osc.setup()
            resetCanvas()

            timer.setup()

            prepareExitHandler()

            // setting up renderer
            resetRenderer()

            isInitialised = true
            return true
        }

        return false
    }

    fun drawFPS(pg: PGraphics) {
        // draw fps
        fpsOverTime += frameRate
        val averageFPS = fpsOverTime / frameCount.toFloat()

        pg.textAlign(PApplet.LEFT, PApplet.BOTTOM)
        pg.fill(255)
        pg.textSize(12f)
        pg.text("FPS: ${frameRate.format(2)}\nFOT: ${averageFPS.format(2)}", 10f, height - 5f)
    }

    fun prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(Thread {
            println("shutting down...")
            renderer.forEach { it.dispose() }
            leapMotion.stop()
            realSense.stop()
            osc.osc.stop()
            artnet.close()

            LogBook.log("Stop", leapMotion.handCount)
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