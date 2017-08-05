package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.artnet.DmxNode
import ch.bildspur.ledforest.controller.ConfigurationController
import ch.bildspur.ledforest.model.AppConfig
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.Universe
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
import processing.core.PApplet
import tornadofx.*
import kotlin.concurrent.thread


class PrimaryView : View(Sketch.NAME) {
    override val root: BorderPane by fxml()

    val tubeMap = TubeMap()

    val moveTool = MoveTool()

    val configuration = ConfigurationController()

    lateinit var appConfig: AppConfig

    lateinit var sketch: Sketch

    lateinit var processingThread: Thread

    @FXML lateinit var elementTreeView: TreeView<TagItem>

    @FXML lateinit var statusLabel: Label

    @FXML lateinit var progressIndicator: ProgressIndicator

    init {
        // setup on shown event
        primaryStage.setOnShown { setupView() }
        root.center = tubeMap

        // exit on main window closed
        primaryStage.setOnCloseRequest {
            sketch.stop()
            processingThread.join(5000)
            System.exit(0)
        }
    }

    fun setupView() {
        // setup ui task
        UITask.status.addListener { o -> statusLabel.text = UITask.status.value }
        UITask.running.addListener { o -> progressIndicator.isVisible = UITask.running.value }

        // setup ui
        UITask.run({
            // init canvas
            tubeMap.setupMap(300.0, 300.0)
            tubeMap.activeTool = moveTool

            // setup treeview
            elementTreeView.selectionModel.selectedItemProperty().addListener { o -> }

            // load app config
            appConfig = configuration.loadAppConfig()

            // start processing
            startProcessing()
        }, { updateUI() }, "startup")
    }

    fun startProcessing() {
        sketch = Sketch()
        sketch.project = createTestConfig()

        processingThread = thread {
            // run processing app
            PApplet.runSketch(arrayOf("Sketch "), sketch)
        }
    }

    fun createTestConfig(): Project {
        val p = Project()
        p.name = "Test Project"
        p.nodes.add(DmxNode("127.0.0.1", listOf(Universe(0), Universe(1))))
        p.tubes.add(Tube(0, 10, 0))
        return p
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

    fun loadProject(file: String) {

    }
}
