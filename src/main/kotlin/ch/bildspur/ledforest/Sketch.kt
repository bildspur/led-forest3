package ch.bildspur.ledforest

import ch.bildspur.ledforest.controller.*
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.util.draw
import ch.bildspur.ledforest.util.format
import org.opencv.core.Core
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.opengl.PJOGL


/**
 * Created by cansik on 04.02.17.
 */
class Sketch : PApplet() {
    companion object {
        @JvmStatic val FRAME_RATE = 30f

        @JvmStatic val WINDOW_WIDTH = 768
        @JvmStatic val WINDOW_HEIGHT = 576

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

    var isUIShown = true

    var enableLights = false

    var isInteractionOn = DataModel(true)

    val peasy = PeasyController(this)

    val osc = OscController(this)

    val config = ConfigurationController(this)

    val timer = TimerController(this)

    val remote = RemoteController(this)

    lateinit var canvas: PGraphics

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

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        surface.setTitle(NAME)

        peasy.setup()
    }

    override fun draw() {
        background(255)

        // skip first two frames
        if (frameCount < 2) {
            peasy.hud {
                textAlign(CENTER, CENTER)
                fill(0)
                textSize(20f)
                text("${Sketch.NAME} is loading...", width / 2f, height / 2f)
            }
            return
        }

        // setup long loading controllers
        if (initControllers()) {
            return
        }

        timer.update()

        canvas.draw {
            it.background(255f)

            // render 3d

            peasy.applyTo(canvas)
        }

        // add hud
        peasy.hud {
            // output image
            image(canvas, 0f, 0f)

            //if (isUIShown)
            // render ui

            drawFPS(g)
        }
    }

    fun initControllers(): Boolean {
        if (!osc.isSetup) {
            osc.setup()

            canvas = createGraphics(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)
            canvas.smooth(8)

            config.setup()
            config.loadConfiguration()

            timer.setup()

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
        pg.fill(0)
        pg.textSize(12f)
        pg.text("FPS: ${frameRate.format(2)}\nFOT: ${averageFPS.format(2)}", 10f, height - 5f)
    }

    fun prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(Thread {
            println("shutting down...")
            osc.osc.stop()
        })
    }

    override fun keyPressed() {
        remote.processCommand(key)
    }
}