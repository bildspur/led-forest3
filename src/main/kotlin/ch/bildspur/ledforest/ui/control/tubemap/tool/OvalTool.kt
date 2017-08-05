package ch.bildspur.ledforest.ui.control.tubemap.tool

import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.fhnw.afpars.ui.control.editor.shapes.OvalShape
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

/**
 * Created by cansik on 29.01.17.
 */
class OvalTool : BaseEditorTool() {
    override val cursor: Cursor
        get() = Cursor.CROSSHAIR

    var dragStart = Point2D.ZERO!!
    var current = OvalShape()

    var defaultFill: Paint = Color.WHITE!!
    var defaultStroke: Paint = Color.BLACK!!
    var defaultStrokeWeight = 1.0

    override fun onCanvasMousePressed(tubeMap: TubeMap, event: MouseEvent) {
        dragStart = Point2D(event.x, event.y)
        current = OvalShape(dragStart, Dimension2D(0.0, 0.0))
        current.fill = defaultFill
        current.stroke = defaultStroke
        current.strokeWeight = defaultStrokeWeight

        tubeMap.addShape(current)
        tubeMap.redraw()
    }

    override fun onCanvasMouseDragged(tubeMap: TubeMap, event: MouseEvent) {
        val points = sortPoints(dragStart, Point2D(event.x, event.y))
        current.size = Dimension2D(points.second.x - points.first.x, points.second.y - points.first.y)
        current.location = points.first
        tubeMap.redraw()
    }

    override fun onCanvasMouseReleased(tubeMap: TubeMap, event: MouseEvent) {
        tubeMap.redraw()
    }
}