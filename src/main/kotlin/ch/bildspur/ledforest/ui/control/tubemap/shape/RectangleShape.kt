package ch.fhnw.afpars.ui.control.editor.shapes

import ch.bildspur.ledforest.util.format
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext

/**
 * Created by cansik on 29.01.17.
 */
class RectangleShape() : BaseShape() {
    var location = Point2D.ZERO!!
    var size = Dimension2D(5.0, 5.0)

    constructor(location: Point2D, size: Dimension2D) : this() {
        this.location = location
        this.size = size
    }

    override fun render(gc: GraphicsContext) {
        gc.fillRect(location.x, location.y, size.width, size.height)
        gc.strokeRect(location.x, location.y, size.width, size.height)
    }

    override fun toString(): String {
        return "Rect (${location.x.format(1)} | ${location.y.format(1)}, w: ${size.width.format(1)}, h: ${size.height.format(1)})"
    }

    override fun contains(point: Point2D): Boolean {
        return location.x <= point.x
                && location.y <= point.y
                && point.x <= location.x + size.width
                && point.y <= location.y + size.height
    }
}