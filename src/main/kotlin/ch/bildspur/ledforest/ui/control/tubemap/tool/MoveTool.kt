package ch.bildspur.ledforest.ui.control.tubemap.tool

import ch.bildspur.ledforest.event.Event
import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.bildspur.ledforest.ui.control.tubemap.shape.TubeShape
import ch.fhnw.afpars.ui.control.editor.shapes.BaseShape
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent

class MoveTool : BaseEditorTool() {
    val shapesSelected = Event<List<BaseShape>>()

    override val cursor: Cursor
        get() = Cursor.DEFAULT

    var shapes: List<TubeShape> = emptyList()

    var dragStart = Point2D.ZERO!!

    override fun onCanvasMouseMoved(tubeMap: TubeMap, event: MouseEvent) {
        val point = Point2D(event.x, event.y)
        val shapes = tubeMap.activeLayer.shapes.filter { it.visible }.filter { it.contains(point) }

        if (shapes.isNotEmpty())
            tubeMap.cursor = Cursor.HAND
        else
            tubeMap.cursor = cursor
    }

    override fun onCanvasMousePressed(tubeMap: TubeMap, event: MouseEvent) {
        if (event.clickCount == 2) {
            // add new tube
            return
        }

        val dragStart = Point2D(event.x, event.y)
        shapes = tubeMap.activeLayer.shapes
                .filterIsInstance<TubeShape>()
                .filter { it.visible }
                .filter { it.contains(dragStart) }

        shapes.forEach { it.marked = true }
        tubeMap.redraw()
    }

    override fun onCanvasMouseDragged(tubeMap: TubeMap, event: MouseEvent) {
        // drag
        val point = Point2D(event.x, event.y)
        val delta = point.subtract(dragStart)

        shapes.forEach {
            // update shape location
            it.location = Point2D(it.location.x + delta.x, it.location.y + delta.y)
        }

        dragStart = point
        tubeMap.redraw()
    }

    override fun onCanvasMouseReleased(tubeMap: TubeMap, event: MouseEvent) {
        shapes.forEach { it.marked = false }
        tubeMap.redraw()
    }

    override fun onCanvasMouseClicked(tubeMap: TubeMap, event: MouseEvent) {
        // check if items selected
        val point = Point2D(event.x, event.y)
        val shapes = tubeMap.activeLayer.shapes.filter { it.visible }.filter { it.contains(point) }

        if (shapes.isNotEmpty())
            shapesSelected(shapes)
    }
}