package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.createRod
import ch.bildspur.ledforest.util.stackMatrix
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PShape

class SceneRenderer(val g: PGraphics, val tubes: List<Tube>) : IRenderer {
    override val timerTask: TimerTask
        get() = TimerTask(0, { render() })

    lateinit var rodShape: PShape

    // view variables
    internal var rodWidth = 1f
    internal var ledLength = 2f
    internal var rodDetail = 5


    override fun setup() {
        rodShape = g.createRod(rodWidth, ledLength, rodDetail)
        rodShape.disableStyle()
    }

    override fun render() {

        // render tubes
        tubes.forEach { t ->
            g.stackMatrix {
                renderTube(t)
            }
        }
    }

    private fun renderTube(tube: Tube) {
        // draw every LED
        for (i in tube.leds.indices) {
            g.pushMatrix()

            // translate position
            g.translate(tube.position.value.x, tube.position.value.y, tube.position.value.z)

            // global rotation
            g.rotateX(tube.rotation.value.x)
            g.rotateY(tube.rotation.value.y)
            g.rotateZ(tube.rotation.value.z)

            // translate height
            g.translate(0f, 0f, (if (tube.inverted.value) -1 else 1) * (i * ledLength))

            // rotate shape
            g.rotateX(PApplet.radians(90f))

            g.noStroke()
            g.fill(tube.leds[i].color.color)

            g.shape(rodShape)
            g.popMatrix()
        }
    }

    override fun dispose() {
    }
}