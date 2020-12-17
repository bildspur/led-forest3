package ch.fhnw.afpars.ui.control.editor.shapes

import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

/**
 * Created by cansik on 29.01.17.
 */
abstract class BaseShape {
    var fill: Paint = Color.WHITE!!
    var stroke: Paint = Color.BLACK!!

    var markedFill: Paint = Color(1.0, 0.64, 0.0, 0.5)
    var markedStroke: Paint = Color(1.0, 0.64, 0.0, 1.0)

    var strokeWeight = 1.0
    var visible = true
    var selectable = true
    var marked = false

    fun noFill() {
        fill = Color.TRANSPARENT
    }

    fun noStroke() {
        stroke = Color.TRANSPARENT
    }

    abstract fun render(gc: GraphicsContext)

    abstract fun contains(point: Point2D): Boolean

    override fun toString(): String {
        return "BaseShape"
    }
}