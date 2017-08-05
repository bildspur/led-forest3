package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.createRod
import ch.bildspur.ledforest.util.stackMatrix
import processing.core.PGraphics
import processing.core.PShape

class SceneRenderer(val g: PGraphics, val tubes: List<Tube>) : IRenderer {

    lateinit var rodShape: PShape

    // view variables
    internal var rodWidth = 1f
    internal var ledLength = 2f
    internal var rodDetail = 5


    override fun setup() {
        rodShape = g.createRod(rodWidth, ledLength, rodDetail)
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
            g.translate(tube.position.x, tube.position.y, tube.position.z)

            g.rotateX(tube.rotation.x)
            g.rotateY(tube.rotation.y)
            g.rotateZ(tube.rotation.z)

            // translate height
            g.translate(0f, 0f, (if (tube.inverted) -1 else 1) * (i * ledLength))

            g.stroke(255)
            g.fill(tube.leds[i].color.color)

            rodShape.disableStyle()
            g.shape(rodShape)
            g.popMatrix()
        }
    }
}