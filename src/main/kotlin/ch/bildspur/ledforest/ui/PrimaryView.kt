package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.configuration.ConfigurationController
import ch.bildspur.ledforest.model.AppConfig
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.DmxNode
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
import javafx.stage.Stage
import processing.core.PApplet
import processing.core.PVector
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread


class PrimaryView {
    lateinit var primaryStage: Stage

    @FXML lateinit var root: BorderPane

    var tubeMap = TubeMap()

    val moveTool = MoveTool()

    val configuration = ConfigurationController()

    val propertiesControl = PropertiesControl()

    var selectedItem: Any? = null

    lateinit var appConfig: AppConfig

    val project = DataModel(Project())

    lateinit var sketch: Sketch

    lateinit var processingThread: Thread

    @FXML lateinit var propertiesPane: TitledPane

    @FXML lateinit var elementTreeView: TreeView<TagItem>

    @FXML lateinit var statusLabel: Label

    @FXML lateinit var progressIndicator: ProgressIndicator

    @FXML lateinit var isRenderingCheck: CheckBox

    @FXML lateinit var isInteractionOnCheck: CheckBox

    @FXML lateinit var iconView: ImageView

    private val appIcon = Image(javaClass.getResourceAsStream("images/LEDForestIcon.png"))
    private val nodeIcon = Image(javaClass.getResourceAsStream("images/ArtnetIcon32.png"))
    private val dmxIcon = Image(javaClass.getResourceAsStream("images/DmxFront16.png"))
    private val tubeIcon = Image(javaClass.getResourceAsStream("images/SimpleTube16.png"))

    init {
    }

    fun setupView() {
        root.center = tubeMap
        propertiesPane.content = propertiesControl

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

            // on map select
            moveTool.shapesSelected += {
                it.filterIsInstance<TubeShape>().map { it.tube }.forEach { t ->
                    elementTreeView.root.children.forEach { a ->
                        a.children.forEach { u ->
                            u.children.forEach { n ->
                                if (n.value!!.item as Tube == t) {
                                    elementTreeView.selectionModel.select(n)
                                }
                            }
                        }
                    }
                }
            }

            // set app icon
            iconView.image = appIcon

            // for updating the property view
            propertiesControl.propertyChanged += {
                updateUI()
            }

            // load app config
            appConfig = configuration.loadAppConfig()

            // create or load configuration
            if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile)))
                project.value = configuration.loadProject(appConfig.projectFile)
            else
                project.value = Project()

            // start processing
            startProcessing()
        }, { updateUI() }, "startup")
    }

    fun startProcessing() {
        sketch = Sketch()

        project.onChanged += {
            sketch.project.value = project.value
        }
        project.fire()

        processingThread = thread {
            // run processing app
            PApplet.runSketch(arrayOf("Sketch "), sketch)
            println("processing quit")
        }

        // setup processing specific variables
        sketch.isInteractionOn.onChanged += { isInteractionOnCheck.isSelected = it }
        isInteractionOnCheck.setOnAction { sketch.isInteractionOn.value = isInteractionOnCheck.isSelected }
        sketch.isInteractionOn.fire()

        sketch.isRendering.onChanged += { isRenderingCheck.isSelected = it }
        isRenderingCheck.setOnAction { sketch.isRendering.value = isRenderingCheck.isSelected }
        sketch.isRendering.fire()
    }

    fun updateUI() {
        // update treeview
        val rootItem = TreeItem(TagItem("elements"))
        rootItem.isExpanded = true

        elementTreeView.isShowRoot = false
        elementTreeView.isEditable = true

        // add nodes
        val tubes = project.value.tubes.groupBy { it.universe.value }
        project.value.nodes.forEach { n ->
            val nodeItem = TreeItem(TagItem(n), ImageView(nodeIcon))
            nodeItem.isExpanded = true
            rootItem.children.add(nodeItem)

            n.universes.forEach { u ->
                val universeItem = TreeItem(TagItem(u), ImageView(dmxIcon))
                universeItem.isExpanded = true
                nodeItem.children.add(universeItem)

                tubes.getOrElse(u.id.value, { emptyList<Tube>() }).forEach { t ->
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
            project.value = Project()
            resetRenderer()
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
                project.value = configuration.loadProject(result.path)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)

                resetRenderer()
            }, { updateUI() }, "load project")
        }
    }

    fun resetRenderer() {
        if (sketch.isInitialised) {
            sketch.isResetRendererProposed = true
        }
    }

    fun rebuildRenderer() {
        sketch.renderer.forEach { it.setup() }
    }

    fun saveProjectAs(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.initialFileName = ""
        fileChooser.title = "Save project..."
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("JSON", "*.json"))

        val result = fileChooser.showSaveDialog(primaryStage)

        if (result != null) {
            UITask.run({
                configuration.saveProject(result.path, project.value)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)
            }, { updateUI() }, "save project")
        }
    }

    fun saveProject(e: ActionEvent) {
        if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile))) {
            UITask.run({
                configuration.saveProject(appConfig.projectFile, project.value)
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
                "Tube" -> project.value.tubes.add(Tube())
                "Universe" -> project.value.nodes.first().universes.add(Universe())
                "Node" -> project.value.nodes.add(DmxNode())
            }

            rebuildRenderer()
            updateUI()
        })
    }

    fun removeElement(e: ActionEvent) {
        if (selectedItem != null) {
            when (selectedItem) {
                is DmxNode -> project.value.nodes.remove(selectedItem as DmxNode)
                is Universe -> project.value.nodes.forEach {
                    if (it.universes.contains(selectedItem as Universe))
                        it.universes.remove(selectedItem as Universe)
                }
                is Tube -> project.value.tubes.remove(selectedItem as Tube)
            }

            rebuildRenderer()
            updateUI()
        }
    }

    fun updateTubeMap() {
        tubeMap.activeLayer.shapes.clear()

        // transform
        val transform = PVector(tubeMap.canvas.width.toFloat() / 2f, tubeMap.canvas.height.toFloat() / 2f)

        // add all tubes
        project.value.tubes.forEach {
            tubeMap.activeLayer.shapes.add(TubeShape(it, transform))
        }

        tubeMap.redraw()
    }

    fun showInteractionSettings(e: ActionEvent) {
        propertiesControl.initView(project.value.interaction)
    }

    fun showProjectSettings(e: ActionEvent) {
        propertiesControl.initView(project.value)
    }

    fun showLightSettings(e: ActionEvent) {
        propertiesControl.initView(project.value.light)
    }
}
