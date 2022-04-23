package ch.bildspur.ledforest.ui

import ch.bildspur.color.HSV
import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.configuration.ConfigurationController
import ch.bildspur.ledforest.model.AppConfig
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.Universe
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.ui.control.scene.TubeScene
import ch.bildspur.ledforest.ui.control.tubemap.TubeMap
import ch.bildspur.ledforest.ui.control.tubemap.shape.TubeShape
import ch.bildspur.ledforest.ui.control.tubemap.tool.MoveTool
import ch.bildspur.ledforest.ui.util.TagItem
import ch.bildspur.ledforest.ui.util.UITask
import ch.bildspur.ledforest.util.FileWatcher
import ch.bildspur.ledforest.util.OSValidator
import ch.bildspur.model.DataModel
import ch.bildspur.ui.fx.PropertiesControl
import ch.fhnw.afpars.ui.control.editor.shapes.RectangleShape
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.value.WritableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.geometry.Side
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import processing.core.PApplet
import processing.core.PVector
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread
import kotlin.system.exitProcess


class PrimaryView {
    lateinit var primaryStage: Stage

    @FXML
    lateinit var root: BorderPane

    var tubeMap = TubeMap()

    lateinit var tubeScene : TubeScene

    val moveTool = MoveTool()

    val configuration = ConfigurationController()

    val propertiesControl = PropertiesControl()

    var selectedItem: Any? = null

    lateinit var appConfig: AppConfig

    val project = DataModel(Project())

    lateinit var sketch: Sketch

    lateinit var processingThread: Thread

    @FXML
    lateinit var propertiesPane: TitledPane

    @FXML
    lateinit var elementTreeView: TreeView<TagItem>

    @FXML
    lateinit var statusLabel: Label

    // quick settings
    @FXML
    lateinit var sceneManagerMenuItem: CheckMenuItem

    @FXML
    lateinit var interactionSceneManager: CheckMenuItem

    @FXML
    lateinit var disableRenderingMenuItem: CheckMenuItem

    @FXML
    lateinit var display2DMapMenuItem: CheckMenuItem

    private val appIcon = Image(javaClass.getResourceAsStream("images/LEDForestIcon.png"))
    private val nodeIcon = Image(javaClass.getResourceAsStream("images/ArtnetIcon32.png"))
    private val dmxIcon = Image(javaClass.getResourceAsStream("images/DmxFront16.png"))
    private val tubeIcon = Image(javaClass.getResourceAsStream("images/SimpleTube16.png"))

    private var hotReloadWatcher = FileWatcher()

    var hasUnsavedChanges = DataModel(false)

    init {
    }

    fun setupView() {
        // setup center maps and scenes
        tubeScene = TubeScene(project)

        val stackPaneScene = StackPane()
        stackPaneScene.children.add(tubeScene.subScene)
        tubeScene.subScene.heightProperty().bind(stackPaneScene.heightProperty())
        tubeScene.subScene.widthProperty().bind(stackPaneScene.widthProperty())

        val tabPane = TabPane()
        tabPane.side = Side.BOTTOM
        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabPane.tabs.addAll(Tab("2D", tubeMap), Tab("3D", stackPaneScene))
        tabPane.selectionModel.select(1)

        root.center = tabPane

        propertiesPane.content = propertiesControl

        // setup ui task
        UITask.status.addListener { _ -> statusLabel.text = UITask.status.value }

        // setup unsaved handler
        hasUnsavedChanges.onChanged += {
            var changesSign = ""
            if (it) changesSign = "*"

            primaryStage.title = "${Sketch.NAME}$changesSign"
        }
        hasUnsavedChanges.fire()

        // setup treeview
        elementTreeView.selectionModel.selectedItemProperty().addListener { _ ->
            val item = elementTreeView.selectionModel.selectedItem
            Platform.runLater {
                if (item != null) {
                    selectedItem = item.value!!.item!!

                    // select tube (and deselect all others)
                    project.value.tubes.forEach { tube ->
                        tube.isSelected.value = tube == item.value!!.item!!
                    }

                    initSettingsView(item.value!!.item!!, item.value!!.item!!.toString())
                }
            }
        }

        // setup hot reload handler
        hotReloadWatcher.onChange += { path ->
            reloadProjectFromFile(path.toString())
        }

        // setup project changed handler
        project.onChanged += { onProjectChanged(it) }

        // setup ui
        UITask.run({ _ ->
            // init canvas
            tubeMap.setupMap(TubeMap.CANVAS_WIDTH, TubeMap.CANVAS_HEIGHT)
            tubeMap.activeTool = moveTool

            // on map select
            moveTool.shapesSelected += {
                if (it.isNotEmpty()) {
                    it.filterIsInstance<TubeShape>().map { it.tube }.forEach { t ->
                        // select element in tree view
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
                } else {
                    // deselect all tubes
                    project.value.tubes.forEach { tube ->
                        tube.isSelected.value = false
                    }
                }
            }

            // for updating the property view
            propertiesControl.propertyChanged += {
                updateUI()
                hasUnsavedChanges.value = true
            }

            // load app config
            appConfig = configuration.loadAppConfig()

            // create or load configuration
            if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile)))
                try {
                    project.value = configuration.loadProject(appConfig.projectFile)
                } catch (ex: Exception) {
                    val alert = Alert(Alert.AlertType.ERROR)
                    alert.title = "Error"
                    alert.headerText = "Could not load configuration file!"
                    alert.contentText = "File: ${appConfig.projectFile}\nMessage: ${ex.message}"
                    alert.showAndWait()
                }
            else
                project.value = Project()

            // change disable preview based on architecture
            if (OSValidator.isMac && OSValidator.isRosetta2) {
                println("LED Forest running in MacOS Silicon (AMD64) in Rosetta2 Mode")
                println("If you experience bugs and crashes please use Eclipse Temurin (AdoptOpenJDK) 17.0.2")
            }

            // add accelerators
            primaryStage.scene.accelerators.put(KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)) { onSaveProject() }

            // start processing
            startProcessing()
        }, {
            updateUI()
            onShowProjectSettings()

        }, "startup")
    }

    fun startProcessing() {
        sketch = Sketch()

        sketch.onSetupFinished += {
            project.value.map.autoScaleMap()
        }

        project.onChanged += {
            sketch.project.value = project.value
        }
        project.fire()

        processingThread = thread {
            // run processing app
            PApplet.runSketch(arrayOf("Sketch "), sketch)
            println("processing quit")
        }
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
                val colorRect = Rectangle(4.0, 16.0, TubeShape.UNIVERSE_COLORS[u.id.value % TubeShape.UNIVERSE_COLORS.size])
                val graphic = HBox(ImageView(dmxIcon), colorRect)
                graphic.spacing = 4.0
                val universeItem = TreeItem(TagItem(u), graphic)
                universeItem.isExpanded = true
                nodeItem.children.add(universeItem)

                tubes.getOrElse(u.id.value) { emptyList<Tube>() }.forEach { t ->
                    val tubeItem = TreeItem(TagItem(t), ImageView(tubeIcon))
                    tubeItem.isExpanded = true
                    universeItem.children.add(tubeItem)
                }
            }
        }

        elementTreeView.root = rootItem
        recreateTubeMap()
    }

    fun onProjectChanged(project: Project) {
        // setup specific handlers for project

        // quick settings
        createBidirectionalMapping(project.isSceneManagerEnabled,
                sceneManagerMenuItem.onActionProperty(),
                sceneManagerMenuItem.selectedProperty())

        createBidirectionalMapping(project.interaction.isInteractionDataEnabled,
                interactionSceneManager.onActionProperty(),
                interactionSceneManager.selectedProperty())

        createBidirectionalMapping(project.visualisation.disableViewRendering,
                disableRenderingMenuItem.onActionProperty(),
                disableRenderingMenuItem.selectedProperty())

        display2DMapMenuItem.setOnAction {
            if (tubeMap.isVisible) {
                tubeMap.isVisible = false
                primaryStage.width = root.width - tubeMap.width
                root.prefWidth = primaryStage.width
                root.children.remove(tubeMap)
            } else {
                tubeMap.isVisible = true
                primaryStage.width = root.width + tubeMap.width
                root.center = tubeMap
            }
        }

        // add redraw of ui
        project.map.mapScaleFactor.onChanged += {
            recreateTubeMap()
        }

        primaryStage.widthProperty().addListener { _, _, _ ->
            recreateTubeMap()
        }

        primaryStage.heightProperty().addListener { _, _, _ ->
            recreateTubeMap()
        }

        // redraw of ui if setting changes
        project.map.showExtendedName.onChanged += {
            recreateTubeMap()
        }

        // add hot-reload support
        hotReloadWatcher.reset(Paths.get(appConfig.projectFile))
    }

    fun <T> createBidirectionalMapping(dataModel: DataModel<T>,
                                       onActionProperty: ObjectProperty<EventHandler<ActionEvent>>,
                                       value: WritableValue<T>) {
        dataModel.onChanged += { value.value = it }
        onActionProperty.set(EventHandler { dataModel.setSilent(value.value) })
        dataModel.fireLatest()
    }

    fun onNewProject() {
        val loader = FXMLLoader(javaClass.getResource("SetupProjectView.fxml"))
        val root1 = loader.load<Any>() as Parent
        val controller = loader.getController<Any>() as SetupProjectView
        val stage = Stage()

        controller.primaryStage = stage
        controller.project = Project()

        stage.setOnShown { controller.setupView() }
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.initStyle(StageStyle.DECORATED)
        stage.title = Sketch.NAME
        stage.scene = Scene(root1)

        // todo: fix dark theme here
        val jMetro = JMetro(Style.LIGHT)
        jMetro.scene = stage.scene

        stage.showAndWait()

        if (!controller.initNewProject)
            return

        // reset current project
        UITask.run({
            appConfig.projectFile = ""

            // create project
            controller.info.clonePattern.create(controller.project, controller.info)

            project.value.autoNameLEDTubes()
            project.value = controller.project

            // rescale map & auto-name rods
            project.value.map.autoScaleMap()
            project.value.autoNameLEDTubes()

            resetRenderer()
        }, { updateUI() }, "new project")
    }

    fun onLoadProject() {
        val fileChooser = FileChooser()
        fileChooser.title = "Select project to load"
        fileChooser.initialFileName = ""
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("JSON", "*.json")
        )

        val result = fileChooser.showOpenDialog(primaryStage)

        if (result != null) {
            reloadProjectFromFile(result.path)
        }
    }

    private fun reloadProjectFromFile(file: String) {
        UITask.run({
            val currentProjectFile = appConfig.projectFile
            try {
                appConfig.projectFile = file
                project.value = configuration.loadProject(file)
                configuration.saveAppConfig(appConfig)
                resetRenderer()
            } catch (ex: Exception) {
                System.err.println("An error happened on loading the project file ${file}: ${ex.message}")
                ex.printStackTrace()
                appConfig.projectFile = currentProjectFile
            }
        }, { updateUI() }, "load project")
    }

    fun resetRenderer() {
        sketch.proposeResetRenderer()
    }

    fun rebuildRenderer() {
        sketch.renderer.forEach { it.setup() }
    }

    fun onSaveProjectAs() {
        val fileChooser = FileChooser()
        fileChooser.initialFileName = "${project.value.name.value}.json"
        fileChooser.title = "Save project..."
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("JSON", "*.json"))

        val result = fileChooser.showSaveDialog(primaryStage)

        if (result != null) {
            UITask.run({
                configuration.saveProject(result.path, project.value)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)
                hasUnsavedChanges.value = false
                hotReloadWatcher.reset(Paths.get(result.path))
            }, { updateUI() }, "save project")
        }
    }

    fun onSaveProject() {
        if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile))) {
            UITask.run({
                configuration.saveProject(appConfig.projectFile, project.value)
                configuration.saveAppConfig(appConfig)
                hasUnsavedChanges.value = false
                hotReloadWatcher.reset(Paths.get(appConfig.projectFile))
            }, { updateUI() }, "save project")
        } else {
            onSaveProjectAs()
        }
    }

    fun addElement() {
        // show selection dialog
        val dialog = ChoiceDialog("Tube", listOf("Tube", "Universe", "Node"))
        dialog.title = "Add Element"
        dialog.headerText = "Add a new element to the scene."
        dialog.contentText = "Choose an element to add:"

        val result = dialog.showAndWait()

        result.ifPresent { elementName ->
            when (elementName) {
                "Tube" -> project.value.tubes.add(Tube())
                "Universe" -> project.value.nodes.first().universes.add(Universe())
                "Node" -> project.value.nodes.add(DmxNode())
            }

            sketch.setupHooks()
            rebuildRenderer()
            updateUI()
        }
    }

    fun removeElement() {
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

    fun recreateTubeMap() {
        tubeMap.activeLayer.shapes.clear()

        // transform
        val transform = PVector(tubeMap.canvas.width.toFloat() / 2f, tubeMap.canvas.height.toFloat() / 2f)
        val scale = project.value.map.mapScaleFactor.value

        // add boundary
        val box = project.value.interaction.interactionBox.value
        val position = Point2D(box.x.toDouble() * -0.5, box.y.toDouble() * -0.5).multiply(scale.toDouble()).add(transform.x.toDouble(), transform.y.toDouble())
        val dimension = Dimension2D(box.x.toDouble() * scale, box.y.toDouble() * scale)
        val bounds = RectangleShape(position, dimension)
        bounds.selectable = false
        bounds.stroke = Color.WHITE
        bounds.strokeWeight = 0.8
        bounds.noFill()

        tubeMap.activeLayer.shapes.add(bounds)

        // add all tubes
        project.value.tubes.forEach {
            tubeMap.activeLayer.shapes.add(TubeShape(it, transform, scale))
        }

        redrawTubeMap()
    }

    fun redrawTubeMap() {
        tubeMap.redraw()
    }

    private fun initSettingsView(value: Any, name: String) {
        propertiesPane.text = name
        propertiesControl.initView(value)
    }

    fun onMenuClicked() {
        println("hello world")
    }

    fun onShowLeapInteractionSettings() {
        initSettingsView(project.value.leapInteraction, "LeapMotion")
    }

    fun onShowRealSenseInteractionSettings() {
        initSettingsView(project.value.realSenseInteraction, "RealSense")
    }

    fun onShowPoseInteractionSettings() {
        initSettingsView(project.value.poseInteraction, "Pose")
    }

    fun onShowPulseSceneSettings() {
        initSettingsView(project.value.pulseScene, "Pulse")
    }

    fun onShowLedaSceneSettings() {
        initSettingsView(project.value.leda, "Leda")
    }

    fun onShowLedaColliderEditor() {
        val editor = LedaColliderEditor(project.value.leda)
        editor.show()
    }

    fun onShowProjectSettings() {
        initSettingsView(project.value, "Project")
    }

    fun onShowLightSettings() {
        initSettingsView(project.value.light, "Art-Net")
    }

    fun onShowstarPatternSettings() {
        initSettingsView(project.value.starPattern, "Star Pattern")
    }

    fun onClose() {
        sketch.exit()
        exitProcess(0)
    }

    fun onResetRenderer() {
        resetRenderer()
    }

    fun onShowAbout() {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "About"
        alert.headerText = "${Sketch.NAME} - ${Sketch.VERSION}"
        alert.contentText = "Developed by Florian Bruggisser 2018.\nUpdated in 2020 & 2022\nwww.bildspur.ch\n\nURI: ${Sketch.URI_NAME}"
        alert.showAndWait()
    }

    fun onShowInteractionSettings() {
        initSettingsView(project.value.interaction, "Interaction")
    }

    fun onShowCloudSceneSettings() {
        initSettingsView(project.value.cloudScene, "Cloud Scene")
    }

    fun onPlayPauseClicked() {
        println("Play Pause")
    }

    fun onShowVisualisationSettings() {
        initSettingsView(project.value.visualisation, "Visualisation")
    }

    fun onShowMapSettings() {
        initSettingsView(project.value.map, "Map")
    }

    fun onShowAudioSettings() {
        initSettingsView(project.value.audio, "Audio")
    }
}
