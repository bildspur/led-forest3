package ch.bildspur.ledforest.ui.control.tubemap.tool

import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 26.01.17.
 */
abstract class BaseEditorTool : IEditorTool {
    override fun onCanvasMouseClicked(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onCanvasMousePressed(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onCanvasMouseReleased(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onCanvasMouseDragged(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onCanvasMouseMoved(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onCanvasScroll(tubeMap: TubeMap, event: ScrollEvent) {}
    override fun onCanvasKeyPressed(tubeMap: TubeMap, event: KeyEvent) {}

    override fun onEditorMouseClicked(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onEditorMousePressed(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onEditorMouseReleased(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onEditorMouseDragged(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onEditorMouseMoved(tubeMap: TubeMap, event: MouseEvent) {}
    override fun onEditorScroll(tubeMap: TubeMap, event: ScrollEvent) {}
    override fun onEditorKeyPressed(tubeMap: TubeMap, event: KeyEvent) {}

    override val cursor: Cursor
        get() = Cursor.DEFAULT

    internal fun sortPoints(a: Point2D, b: Point2D): Pair<Point2D, Point2D> {
        val x1 = if (a.x < b.x) a.x else b.x
        val x2 = if (a.x > b.x) a.x else b.x

        val y1 = if (a.y < b.y) a.y else b.y
        val y2 = if (a.y > b.y) a.y else b.y

        return Pair(Point2D(x1, y1), Point2D(x2, y2))
    }
}