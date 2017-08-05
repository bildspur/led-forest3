package ch.fhnw.afpars.ui.control.editor.shapes

import ch.bildspur.ledforest.util.format
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext

/**
 * Created by cansik on 29.01.17.
 */
open class OvalShape() : BaseShape() {
    var location = Point2D.ZERO!!
    var size = Dimension2D(5.0, 5.0)

    constructor(center: Point2D, size: Dimension2D) : this() {
        this.location = center
        this.size = size
    }

    override fun render(gc: GraphicsContext) {
        gc.fillOval(location.x, location.y, size.width, size.height)
        gc.strokeOval(location.x, location.y, size.width, size.height)
    }

    override fun toString(): String {
        return "Oval (${location.x.format(1)} | ${location.y.format(1)}, w: ${size.width.format(1)}, h: ${size.height.format(1)})"
    }

    override fun contains(point: Point2D): Boolean {
        val ellw = size.width
        if (ellw <= 0.0) {
            return false
        }
        val normx = (point.x - location.x) / ellw - 0.5
        val ellh = size.height
        if (ellh <= 0.0) {
            return false
        }
        val normy = (point.y - location.y) / ellh - 0.5
        return normx * normx + normy * normy < 0.25
    }
}