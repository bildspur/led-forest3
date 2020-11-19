package ch.bildspur.ledforest.ui.control.tubemap.tool

import ch.bildspur.event.Event
import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.fhnw.afpars.ui.control.editor.shapes.BaseShape
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 25.01.17.
 */
class ViewTool : BaseEditorTool() {
    val scaleSpeed = 1.0 / 50.0

    var dragStart = Point2D.ZERO!!

    val shapesSelected = Event<List<BaseShape>>()

    override val cursor: Cursor
        get() = Cursor.OPEN_HAND

    override fun onEditorMousePressed(tubeMap: TubeMap, event: MouseEvent) {
        if (event.clickCount == 2) {
            tubeMap.resetZoom()
            return
        }

        dragStart = Point2D(event.x, event.y)
    }

    override fun onEditorMouseDragged(tubeMap: TubeMap, event: MouseEvent) {
        // drag
        val point = Point2D(event.x, event.y)
        val delta = dragStart.subtract(point)

        tubeMap.canvasTransformation = delta.multiply(-1.0)
        dragStart = point

        tubeMap.resize()
    }

    override fun onCanvasMouseClicked(tubeMap: TubeMap, event: MouseEvent) {
        // check if items selected
        val point = Point2D(event.x, event.y)
        val shapes = tubeMap.layers.flatMap { it.shapes.filter { it.visible }.filter { it.contains(point) } }

        if (shapes.isNotEmpty())
            shapesSelected(shapes)
    }

    override fun onEditorScroll(tubeMap: TubeMap, event: ScrollEvent) {
        // zoom point
        tubeMap.zoomTransformation = Point2D(event.x, event.y)

        // scale
        tubeMap.zoomScale += -1 * event.deltaY * scaleSpeed
        tubeMap.zoomScale = Math.min(Math.max(tubeMap.minimumZoom, tubeMap.zoomScale), tubeMap.maximumZoom)
        tubeMap.resize()
    }
}