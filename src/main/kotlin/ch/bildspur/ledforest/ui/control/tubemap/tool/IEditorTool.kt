package ch.bildspur.ledforest.ui.control.tubemap.tool

import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import javafx.scene.Cursor
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 25.01.17.
 */
interface IEditorTool {
    fun onCanvasMouseClicked(tubeMap: TubeMap, event: MouseEvent)
    fun onCanvasMousePressed(tubeMap: TubeMap, event: MouseEvent)
    fun onCanvasMouseReleased(tubeMap: TubeMap, event: MouseEvent)
    fun onCanvasMouseDragged(tubeMap: TubeMap, event: MouseEvent)
    fun onCanvasMouseMoved(tubeMap: TubeMap, event: MouseEvent)
    fun onCanvasScroll(tubeMap: TubeMap, event: ScrollEvent)
    fun onCanvasKeyPressed(tubeMap: TubeMap, event: KeyEvent)

    fun onEditorMouseClicked(tubeMap: TubeMap, event: MouseEvent)
    fun onEditorMousePressed(tubeMap: TubeMap, event: MouseEvent)
    fun onEditorMouseReleased(tubeMap: TubeMap, event: MouseEvent)
    fun onEditorMouseDragged(tubeMap: TubeMap, event: MouseEvent)
    fun onEditorMouseMoved(tubeMap: TubeMap, event: MouseEvent)
    fun onEditorScroll(tubeMap: TubeMap, event: ScrollEvent)
    fun onEditorKeyPressed(tubeMap: TubeMap, event: KeyEvent)

    val cursor: Cursor
        get
}