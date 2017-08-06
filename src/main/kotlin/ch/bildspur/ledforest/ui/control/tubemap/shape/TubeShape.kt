package ch.bildspur.ledforest.ui.control.tubemap.shape

import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.format
import ch.fhnw.afpars.ui.control.editor.shapes.OvalShape
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext

class TubeShape(val tube: Tube) : OvalShape() {

    init {
        location = Point2D(tube.position.x.toDouble(), tube.position.y.toDouble())
        size = Dimension2D(20.0, 20.0)
    }

    fun locationChanged() {

    }

    override fun render(gc: GraphicsContext) {
        val exact = Point2D(location.x - (size.width / 2f), location.y - (size.height / 2f))
        gc.fillOval(exact.x, exact.y, size.width, size.height)
        gc.strokeOval(exact.x, exact.y, size.width, size.height)
    }

    override fun toString(): String {
        return "Tube (${location.x.format(1)} | ${location.y.format(1)}, w: ${size.width.format(1)}, h: ${size.height.format(1)})"
    }
}