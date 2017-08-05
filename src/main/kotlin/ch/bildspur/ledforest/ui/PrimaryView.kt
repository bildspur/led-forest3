package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.bildspur.ledforest.ui.control.tubemap.tool.MoveTool
import ch.bildspur.ledforest.ui.util.TagItem
import ch.bildspur.ledforest.ui.util.UITask
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TreeView
import javafx.scene.layout.BorderPane
import tornadofx.*


class PrimaryView : View(Sketch.NAME) {
    override val root: BorderPane by fxml()

    val tubeMap = TubeMap()

    val viewTool = MoveTool()

    @FXML lateinit var elementTreeView: TreeView<TagItem>

    @FXML lateinit var statusLabel: Label

    @FXML lateinit var progressIndicator: ProgressIndicator

    init {
        // setup on shown event
        primaryStage.setOnShown { setupView() }
        root.center = tubeMap
    }

    fun setupView() {
        // setup ui task
        UITask.status.addListener { o -> statusLabel.text = UITask.status.value }
        UITask.running.addListener { o -> progressIndicator.isVisible = UITask.running.value }

        // setup ui
        UITask.run({
            // init canvas
            tubeMap.prefWidth(100.0)
            tubeMap.prefWidth(100.0)

            tubeMap.setupMap(300.0, 300.0)
            tubeMap.activeTool = viewTool

            // setup treeview
            elementTreeView.selectionModel.selectedItemProperty().addListener { o -> }
        }, { updateUI() }, "startup")
    }

    fun updateUI() {

    }

    fun newProject(e: ActionEvent) {

    }

    fun loadProject(e: ActionEvent) {

    }

    fun saveProject(e: ActionEvent) {

    }

    fun addTube(e: ActionEvent) {

    }

    fun removeTube(e: ActionEvent) {

    }
}
