package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.Sketch
import ch.bildspur.math.max
import peasy.CameraState
import peasy.PeasyCam
import processing.core.PApplet
import processing.core.PConstants.PI
import processing.core.PGraphics


/**
 * Created by cansik on 08.06.17.
 */
class PeasyController(internal var sketch: Sketch) {
    lateinit var cam: PeasyCam

    var isOrtho = false
    var stateSwitchSpeed = 200L

    private lateinit var defaultState: CameraState

    private lateinit var topState: CameraState
    private lateinit var frontState: CameraState
    private lateinit var leftState: CameraState
    private lateinit var rightState: CameraState

    private val minDistance = 0.0
    private val maxDistance = 500.0

    fun setup() {
        cam = PeasyCam(sketch, 0.0, 0.0, 0.0, maxDistance * 0.8)

        cam.setMinimumDistance(minDistance)
        cam.setMaximumDistance(maxDistance)

        initStates()
        defaultView()
    }

    fun applyTo(canvas: PGraphics) {
        if (isOrtho) {
            val zoom = ((cam.distance - minDistance) / (maxDistance - minDistance)).toFloat()
            sketch.canvas.ortho(-sketch.width / 2 * zoom, sketch.width / 2 * zoom, -sketch.height / 2 * zoom, sketch.height / 2 * zoom, -100f, 1000f)
        } else {
            sketch.canvas.perspective(PI / 2.5f, sketch.width.toFloat() / sketch.height, 0.001f, 1000f)
        }

        cam.state.apply(canvas)
    }

    fun hud(block: () -> Unit) {
        cam.beginHUD()
        block()
        cam.endHUD()
    }

    fun disable() {
        cam.isActive = false
    }

    fun enable() {
        cam.isActive = true
    }

    // view settings
    fun perspective() {
        isOrtho = false
    }

    fun ortho() {
        isOrtho = true
    }

    // predefined views
    fun topView() {
        cam.setState(topState, stateSwitchSpeed)
    }

    fun frontView() {
        cam.setState(frontState, stateSwitchSpeed)
    }

    fun leftView() {
        cam.setState(leftState, stateSwitchSpeed)
    }

    fun rightView() {
        cam.setState(rightState, stateSwitchSpeed)
    }

    fun defaultView() {
        cam.setState(defaultState, stateSwitchSpeed)
    }

    private fun initStates() {
        // top state
        topState = cam.state

        // default state
        cam.rotateX(PApplet.radians(-75f).toDouble())
        defaultState = cam.state
        cam.rotateX(PApplet.radians(75f).toDouble())

        // front state
        cam.rotateX(PApplet.radians(-90f).toDouble())
        frontState = cam.state

        // left state
        cam.rotateY(PApplet.radians(-90f).toDouble())
        leftState = cam.state

        // right state
        cam.rotateY(PApplet.radians(180f).toDouble())
        rightState = cam.state
    }
}