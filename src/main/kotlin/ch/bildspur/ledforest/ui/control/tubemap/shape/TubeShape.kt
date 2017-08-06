package ch.bildspur.ledforest.ui.control.tubemap.shape

import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.format
import ch.fhnw.afpars.ui.control.editor.shapes.OvalShape
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import processing.core.PVector

class TubeShape(val tube: Tube) : OvalShape() {
    val universeColors = arrayOf(
            ColorMode.color(208, 100, 85),
            ColorMode.color(127, 77, 80),
            ColorMode.color(52, 100, 100),
            ColorMode.color(28, 89, 100),
            ColorMode.color(3, 79, 100),
            ColorMode.color(314, 93, 94),
            ColorMode.color(197, 50, 100),
            ColorMode.color(146, 100, 100),
            ColorMode.color(292, 94, 79),
            ColorMode.color(0, 0, 87)
    )

    init {
        location = tube.position.project()
        size = Dimension2D(10.0, 10.0)
        stroke = Color.BEIGE
    }

    fun updateLocation() {
        tube.position = location.project()
    }

    override fun render(gc: GraphicsContext) {
        val color = universeColors [tube.universe % universeColors.size]
        gc.fill = Color.rgb(ColorMode.red(color).toInt(), ColorMode.green(color).toInt(), ColorMode.blue(color).toInt())

        val exact = Point2D(location.x - (size.width / 2.0), location.y - (size.height / 2.0))
        gc.fillOval(exact.x, exact.y, size.width, size.height)
        gc.strokeOval(exact.x, exact.y, size.width, size.height)
    }

    override fun toString(): String {
        return "Tube (${location.x.format(1)} | ${location.y.format(1)}, w: ${size.width.format(1)}, h: ${size.height.format(1)})"
    }

    private fun PVector.project(): Point2D {
        return Point2D(this.x.toDouble(), this.y.toDouble())
    }

    private fun Point2D.project(): PVector {
        return PVector(this.x.toFloat(), this.y.toFloat(), tube.position.z)
    }
}