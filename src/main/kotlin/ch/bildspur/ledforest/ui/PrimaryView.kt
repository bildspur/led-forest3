package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.artnet.DmxNode
import ch.bildspur.ledforest.configuration.ConfigurationController
import ch.bildspur.ledforest.model.AppConfig
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.Universe
import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.bildspur.ledforest.ui.control.tubemap.shape.TubeShape
import ch.bildspur.ledforest.ui.control.tubemap.tool.MoveTool
import ch.bildspur.ledforest.ui.properties.PropertiesControl
import ch.bildspur.ledforest.ui.util.TagItem
import ch.bildspur.ledforest.ui.util.UITask
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import processing.core.PApplet
import tornadofx.*
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread


class PrimaryView : View(Sketch.NAME) {
    override val root: BorderPane by fxml()

    val tubeMap = TubeMap()

    val moveTool = MoveTool()

    val configuration = ConfigurationController()

    val propertiesControl = PropertiesControl()

    var selectedItem: Any? = null

    lateinit var appConfig: AppConfig

    lateinit var project: Project

    lateinit var sketch: Sketch

    lateinit var processingThread: Thread

    @FXML lateinit var propertiesPane: TitledPane

    @FXML lateinit var elementTreeView: TreeView<TagItem>

    @FXML lateinit var statusLabel: Label

    @FXML lateinit var progressIndicator: ProgressIndicator

    private val nodeIcon = Image(javaClass.getResourceAsStream("images/ArtnetIcon32.png"))
    private val dmxIcon = Image(javaClass.getResourceAsStream("images/DmxFront16.png"))
    private val tubeIcon = Image(javaClass.getResourceAsStream("images/SimpleTube16.png"))

    init {
        // setup on shown event
        primaryStage.setOnShown { setupView() }
        root.center = tubeMap
        propertiesPane.content = propertiesControl

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

        // setup treeview
        elementTreeView.selectionModel.selectedItemProperty().addListener { o ->
            val item = elementTreeView.selectionModel.selectedItem
            Platform.runLater {
                if (item != null) {
                    selectedItem = item.value!!.item!!
                    propertiesControl.initView(item.value!!.item!!)
                }
            }
        }

        // setup ui
        UITask.run({
            // init canvas
            tubeMap.setupMap(300.0, 300.0)
            tubeMap.activeTool = moveTool

            // for updating the property view
            propertiesControl.propertyChanged += {
                // special fix for led tube
                if (selectedItem != null && selectedItem is Tube) {
                    val tube = selectedItem as Tube

                    // check if led has to be reinitialised
                    if (tube.ledCount != tube.leds.size) {
                        tube.initLEDs()
                        println("had to reinit")
                    }
                }

                updateUI()
            }

            // load app config
            appConfig = configuration.loadAppConfig()

            // create or load configuration
            if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile)))
                project = configuration.loadProject(appConfig.projectFile)
            else
                project = Project()

            // start processing
            startProcessing()
        }, { updateUI() }, "startup")
    }

    fun startProcessing() {
        sketch = Sketch()
        sketch.project = project

        processingThread = thread {
            // run processing app
            PApplet.runSketch(arrayOf("Sketch "), sketch)
        }
    }

    fun updateUI() {
        // update treeview
        val rootItem = TreeItem(TagItem("elements"))
        rootItem.isExpanded = true

        elementTreeView.isShowRoot = false
        elementTreeView.isEditable = true

        // add nodes
        val tubes = project.tubes.groupBy { it.universe }
        project.nodes.forEach { n ->
            val nodeItem = TreeItem(TagItem(n), ImageView(nodeIcon))
            nodeItem.isExpanded = true
            rootItem.children.add(nodeItem)

            n.universes.forEach { u ->
                val universeItem = TreeItem(TagItem(u), ImageView(dmxIcon))
                universeItem.isExpanded = true
                nodeItem.children.add(universeItem)

                tubes.getOrElse(u.id, { emptyList<Tube>() }).forEach { t ->
                    val tubeItem = TreeItem(TagItem(t), ImageView(tubeIcon))
                    tubeItem.isExpanded = true
                    universeItem.children.add(tubeItem)
                }
            }
        }

        elementTreeView.root = rootItem
        updateTubeMap()
    }

    fun newProject(e: ActionEvent) {
        UITask.run({
            appConfig.projectFile = ""
            project = Project()
        }, { updateUI() }, "new project")
    }

    fun loadProject(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.title = "Select project to load"
        fileChooser.initialFileName = ""
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("JSON", "*.json")
        )

        val result = fileChooser.showOpenDialog(primaryStage)

        if (result != null) {
            UITask.run({
                project = configuration.loadProject(result.path)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)

                resetupRenderer()
            }, { updateUI() }, "load project")
        }
    }

    fun resetupRenderer() {
        sketch.renderer.forEach { it.setup() }
    }

    fun saveProject(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.initialFileName = ""
        fileChooser.title = "Save project..."
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("JSON", "*.json"))

        val result = fileChooser.showSaveDialog(primaryStage)

        if (result != null) {
            UITask.run({
                configuration.saveProject(result.path, project)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)
            }, { updateUI() }, "save project")
        }
    }

    fun addElement(e: ActionEvent) {
        // show selection dialog
        val dialog = ChoiceDialog("Tube", listOf("Tube", "Universe", "Node"))
        dialog.title = "Add Element"
        dialog.headerText = "Add a new element to the scene."
        dialog.contentText = "Choose an element to add:"

        val result = dialog.showAndWait()

        result.ifPresent({ elementName ->
            when (elementName) {
                "Tube" -> project.tubes.add(Tube(0, 0))
                "Universe" -> project.nodes.first().universes.add(Universe(0))
                "Node" -> project.nodes.add(DmxNode("127.0.0.1", mutableListOf()))
            }

            resetupRenderer()
            updateUI()
        })
    }

    fun removeElement(e: ActionEvent) {
        if (selectedItem != null) {
            when (selectedItem) {
                is DmxNode -> project.nodes.remove(selectedItem as DmxNode)
                is Universe -> project.nodes.forEach {
                    if (it.universes.contains(selectedItem as Universe))
                        it.universes.remove(selectedItem as Universe)
                }
                is Tube -> project.tubes.remove(selectedItem as Tube)
            }

            resetupRenderer()
            updateUI()
        }
    }

    fun updateTubeMap() {
        tubeMap.activeLayer.shapes.clear()

        // add all tubes
        project.tubes.forEach {
            tubeMap.activeLayer.shapes.add(TubeShape(it))
        }

        tubeMap.redraw()
    }
}
