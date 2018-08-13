package ch.bildspur.ledforest.ui.control.tubemap.shape

import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.format
import ch.fhnw.afpars.ui.control.editor.shapes.OvalShape
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import processing.core.PVector

class TubeShape(val tube: Tube, val transform: PVector = PVector()) : OvalShape() {
    companion object {
        @JvmStatic
        val UNIVERSE_COLORS = arrayOf(
                Color.rgb(230, 25, 75),
                Color.rgb(60, 180, 75),
                Color.rgb(255, 225, 25),
                Color.rgb(0, 130, 200),
                Color.rgb(245, 130, 48),
                Color.rgb(145, 30, 180),
                Color.rgb(70, 240, 240),
                Color.rgb(240, 50, 230),
                Color.rgb(210, 245, 60),
                Color.rgb(250, 190, 190),
                Color.rgb(0, 128, 128),
                Color.rgb(230, 190, 255),
                Color.rgb(170, 110, 40),
                Color.rgb(255, 250, 200),
                Color.rgb(128, 0, 0),
                Color.rgb(170, 255, 195),
                Color.rgb(128, 128, 0),
                Color.rgb(255, 215, 180),
                Color.rgb(0, 0, 128)
        )
    }

    val font = Font("PT Mono", 8.0)

    init {
        location = tube.position.value.project()
        size = Dimension2D(10.0, 10.0)
        stroke = Color.DARKGRAY
    }

    fun updateLocation() {
        tube.position.value = location.project()
    }

    override fun render(gc: GraphicsContext) {
        gc.fill = UNIVERSE_COLORS[tube.universe.value % UNIVERSE_COLORS.size]

        val exact = Point2D(location.x - (size.width / 2.0), location.y - (size.height / 2.0))
        gc.fillOval(exact.x, exact.y, size.width, size.height)
        gc.strokeOval(exact.x, exact.y, size.width, size.height)

        gc.fill = Color.DARKGRAY
        gc.textAlign = TextAlignment.LEFT
        gc.font = font
        gc.fillText("$tube", exact.x + 10.0, exact.y + 15.0)
    }

    override fun contains(point: Point2D): Boolean {
        return location.distance(point) <= size.width
    }

    override fun toString(): String {
        return "Tube (${location.x.format(1)} | ${location.y.format(1)}, w: ${size.width.format(1)}, h: ${size.height.format(1)})"
    }

    private fun PVector.project(): Point2D {
        return Point2D(this.x.toDouble() + transform.x, this.y.toDouble() + transform.y)
    }

    private fun Point2D.project(): PVector {
        return PVector(this.x.toFloat() - transform.x, this.y.toFloat() - transform.y, tube.position.value.z)
    }
}