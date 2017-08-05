package ch.bildspur.ledforest

import ch.bildspur.ledforest.artnet.ArtNetClient
import ch.bildspur.ledforest.controller.OscController
import ch.bildspur.ledforest.controller.PeasyController
import ch.bildspur.ledforest.controller.RemoteController
import ch.bildspur.ledforest.controller.TimerController
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.util.draw
import ch.bildspur.ledforest.util.format
import ch.bildspur.ledforest.view.ArtNetRenderer
import ch.bildspur.ledforest.view.IRenderer
import ch.bildspur.ledforest.view.SceneRenderer
import ch.bildspur.postfx.builder.PostFX
import org.opencv.core.Core
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.opengl.PJOGL


/**
 * Created by cansik on 04.02.17.
 */
class Sketch() : PApplet() {
    companion object {
        @JvmStatic val FRAME_RATE = 60f

        @JvmStatic val WINDOW_WIDTH = 768
        @JvmStatic val WINDOW_HEIGHT = 576

        @JvmStatic val CURSOR_HIDING_TIME = 5000

        @JvmStatic val NAME = "LED Forest 3"

        @JvmStatic lateinit var instance: PApplet

        @JvmStatic fun map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double {
            return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
        }

        @JvmStatic fun currentMillis(): Int {
            return instance.millis()
        }
    }

    var fpsOverTime = 0f

    var isStatusViewShown = false

    var isInteractionOn = DataModel(true)

    val peasy = PeasyController(this)

    val osc = OscController(this)

    val timer = TimerController(this)

    val remote = RemoteController(this)

    val artnet = ArtNetClient()

    lateinit var canvas: PGraphics

    var lastCursorMoveTime = 0

    val renderer = mutableListOf<IRenderer>()

    var project = Project()

    lateinit var fx: PostFX

    init {
    }

    override fun settings() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)
        PJOGL.profile = 1
    }

    override fun setup() {
        Sketch.instance = this
        smooth()

        frameRate(FRAME_RATE)
        colorMode(HSB, 360f, 100f, 100f)

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        surface.setTitle("$NAME - ${project.name}")

        fx = PostFX(this)

        peasy.setup()
        artnet.open()

        // add renderer
        renderer.add(SceneRenderer(this.g, project.tubes))
        renderer.add(ArtNetRenderer(artnet, project.nodes, project.tubes))

        // timer for cursor hiding
        timer.addTask(TimerTask(CURSOR_HIDING_TIME, {
            val current = millis()
            if (current - lastCursorMoveTime > CURSOR_HIDING_TIME)
                noCursor()
        }))
    }

    override fun draw() {
        background(0)

        if (skipFirstFrames())
            return

        // setup long loading controllers
        if (initControllers())
            return

        // update timer and tubes
        timer.update()
        project.tubes.forEach { it.leds.forEach { it.color.update() } }

        canvas.draw {
            it.background(0)

            // render
            renderer.forEach { it.render() }

            peasy.applyTo(canvas)
        }

        // add hud
        peasy.hud {
            // output image
            fx.render(canvas)
                    .bloom(0.1f, 20, 40f)
                    .rgbSplit(100f)
                    .compose()
            drawFPS(g)
        }
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

            canvas = createGraphics(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)
            canvas.smooth(8)

            timer.setup()

            // setup renderer
            renderer.forEach { it.setup() }

            prepareExitHandler()

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
            osc.osc.stop()
            artnet.close()
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