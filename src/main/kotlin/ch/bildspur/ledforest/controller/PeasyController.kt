package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.Sketch
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
    var zoomRatio = 1.0f

    fun setup() {
        cam = PeasyCam(sketch, 0.0, 0.0, 0.0, 200.0)

        cam.setMinimumDistance(0.0)
        cam.setMaximumDistance(500.0)

        //cam.rotateZ(PApplet.radians(-90f).toDouble())
        cam.rotateX(PApplet.radians(-75f).toDouble())
    }

    fun applyTo(canvas: PGraphics) {
        if(isOrtho) {
            sketch.canvas.ortho(-sketch.width / 2 * zoomRatio,sketch.width / 2 * zoomRatio, -sketch.height / 2 * zoomRatio, sketch.height / 2*zoomRatio, -100f, 1000f)
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

    // predefined view
    fun topView() {

    }

    fun frontView() {

    }

    fun sideView() {

    }
}