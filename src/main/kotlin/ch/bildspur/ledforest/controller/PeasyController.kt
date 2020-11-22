package ch.bildspur.ledforest.controller

import peasy.PeasyCam
import processing.core.PApplet
import processing.core.PConstants.PI
import processing.core.PGraphics
import javax.swing.Spring.height





/**
 * Created by cansik on 08.06.17.
 */
class PeasyController(internal var sketch: PApplet) {
    lateinit var cam: PeasyCam

    fun setup() {
        cam = PeasyCam(sketch, 0.0, 0.0, 0.0, 200.0)

        cam.setMinimumDistance(0.0)
        cam.setMaximumDistance(500.0)

        //cam.rotateZ(PApplet.radians(-90f).toDouble())
        cam.rotateX(PApplet.radians(-75f).toDouble())

        // remove clipping in scene
        sketch.perspective(PI / 2.5f, sketch.width.toFloat() / sketch.height, 0.001f, 1000f)
    }

    fun applyTo(canvas: PGraphics) {
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
}