package ch.bildspur.ledforest.ui.control.tubemap.tool

import ch.bildspur.ledforest.event.Event
import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.fhnw.afpars.ui.control.editor.shapes.BaseShape
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent

class MoveTool : BaseEditorTool() {
    val shapesSelected = Event<List<BaseShape>>()

    override val cursor: Cursor
        get() = Cursor.DEFAULT

    override fun onEditorMousePressed(tubeMap: TubeMap, event: MouseEvent) {
        if (event.clickCount == 2) {
            // add new tube
            return
        }
    }

    override fun onCanvasMouseClicked(tubeMap: TubeMap, event: MouseEvent) {
        // check if items selected
        val point = Point2D(event.x, event.y)
        val shapes = tubeMap.layers.flatMap { it.shapes.filter { it.visible }.filter { it.contains(point) } }

        if (shapes.isNotEmpty())
            shapesSelected(shapes)
    }
}