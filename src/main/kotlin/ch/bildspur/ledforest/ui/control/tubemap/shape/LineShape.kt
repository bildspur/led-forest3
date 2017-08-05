package ch.fhnw.afpars.ui.control.editor.shapes

import ch.bildspur.ledforest.util.format
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext

/**
 * Created by cansik on 29.01.17.
 */
class LineShape() : BaseShape() {
    var point1 = Point2D.ZERO!!
    var point2 = Point2D.ZERO!!

    constructor(point1: Point2D, point2: Point2D) : this() {
        this.point1 = point1
        this.point2 = point2
    }

    override fun render(gc: GraphicsContext) {
        gc.strokeLine(point1.x, point1.y, point2.x, point2.y)
    }

    override fun toString(): String {
        return "Line (${point1.x.format(1)} | ${point1.y.format(1)}, ${point2.x.format(1)} | ${point2.y.format(1)})"
    }

    override fun contains(point: Point2D): Boolean {
        val x1 = point1.x
        val x2 = point2.x
        val y1 = point1.y
        val y2 = point2.y

        val x = point.x
        val y = point.y

        val AB = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
        val AP = Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1))
        val PB = Math.sqrt((x2 - x) * (x2 - x) + (y2 - y) * (y2 - y))
        return AB == AP + PB
    }
}