package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.ui.util.TagItem
import ch.bildspur.ui.fx.PropertiesControl
import ch.bildspur.ui.fx.utils.toFXColor
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import tornadofx.selectFirst


class LedaColliderEditor(val project: Project) : Stage() {
    private val config = project.leda

    private val root = BorderPane()
    private val treeView = TreeView<TagItem>()
    private val propertiesControl = PropertiesControl()

    private val colliderIcon = Image(javaClass.getResourceAsStream("images/Collider32.png"))
    private val pulseIcon = Image(javaClass.getResourceAsStream("images/Pulse162.png"))

    init {
        title = "Leda Colliders"

        scene = Scene(root, 600.0, 600.0)
        this.scene = scene

        treeView.isShowRoot = false
        treeView.isEditable = true

        val scrollPane = ScrollPane()
        scrollPane.isFitToHeight = true
        scrollPane.isFitToWidth = true
        scrollPane.content = treeView

        val testButton = Button("Test Deploy")
        testButton.setOnAction {
            val item = treeView.selectionModel.selectedItem
            if (item != null) {
                deployTest(item.value!!.item!!)
            }
        }

        val topBox = HBox(testButton)
        topBox.spacing = 10.0

        root.top = topBox
        root.center = scrollPane
        root.right = ScrollPane(propertiesControl)

        updateTreeView()

        val jMetro = JMetro(Style.DARK)
        jMetro.scene = scene

        treeView.selectionModel.selectedItemProperty().addListener { _ ->
            val item = treeView.selectionModel.selectedItem
            Platform.runLater {
                if (item != null) {
                    val selectedItem = item.value!!.item!!
                    propertiesControl.initView(selectedItem)
                }
            }
        }

        treeView.selectFirst()
    }

    fun updateTreeView() {
        val rootItem = TreeItem(TagItem("elements"))
        rootItem.isExpanded = true

        config.landmarkColliders.forEach { c ->
            val colliderItem = TreeItem(TagItem(c), ImageView(colliderIcon))
            colliderItem.isExpanded = true
            rootItem.children.add(colliderItem)

            c.pulses.forEach { p ->
                val pulseItem = TreeItem(TagItem(p), Rectangle(16.0, 16.0, p.color.value.toFXColor()))
                pulseItem.isExpanded = false
                colliderItem.children.add(pulseItem)
            }
        }

        treeView.root = rootItem
    }

    private fun deployTest(obj: Any) {
        if (obj is LandmarkPulseCollider) {
            obj.pulses.forEach { deployPulse(it) }
        } else if (obj is Pulse) {
            deployPulse(obj)
        }
    }

    private fun deployPulse(pulse: Pulse) {
        project.pulseScene.pulses.add(pulse.spawn())
    }
}