package ch.bildspur.ledforest.ui.control.tubemap.tool

import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.fhnw.afpars.ui.control.editor.shapes.LineShape
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

/**
 * Created by cansik on 25.01.17.
 */
open class LineTool : BaseEditorTool() {
    var dragStart = Point2D.ZERO!!
    var current = LineShape()

    var defaultStroke: Paint = Color.BLACK!!
    var defaultStrokeWeight = 1.0

    override val cursor: Cursor
        get() = Cursor.CROSSHAIR

    override fun onCanvasMousePressed(tubeMap: TubeMap, event: MouseEvent) {
        dragStart = Point2D(event.x, event.y)
        current = LineShape(dragStart, dragStart)
        current.stroke = defaultStroke
        current.strokeWeight = defaultStrokeWeight

        tubeMap.addShape(current)
        tubeMap.redraw()
    }

    override fun onCanvasMouseDragged(tubeMap: TubeMap, event: MouseEvent) {
        current.point2 = Point2D(event.x, event.y)
        tubeMap.redraw()
    }

    override fun onCanvasMouseReleased(tubeMap: TubeMap, event: MouseEvent) {
        tubeMap.redraw()
    }
}