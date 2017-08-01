package ch.bildspur.floje.controller

import peasy.PeasyCam
import processing.core.PApplet
import processing.core.PGraphics


/**
 * Created by cansik on 08.06.17.
 */
class PeasyController(internal var sketch: PApplet) {
    lateinit var cam: PeasyCam

    fun setup() {
        cam = PeasyCam(sketch, 0.0, 0.0, 0.0, 600.0)

        cam.setMinimumDistance(200.0)
        cam.setMaximumDistance(800.0)

        cam.rotateZ(PApplet.radians(-90f).toDouble())
        cam.rotateX(PApplet.radians(-75f).toDouble())
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