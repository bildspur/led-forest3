package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.scene.BaseScene
import ch.bildspur.ledforest.scene.SceneRegistry
import ch.bildspur.model.ListDataModel
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import org.controlsfx.control.CheckListView
import tornadofx.add


class SceneSelectorDialog(val scenes: ListDataModel<BaseScene>) : Stage() {
    private val root = BorderPane()

    private val items = FXCollections.observableArrayList<BaseScene>()
    private val listView = CheckListView(items)

    init {
        title = "Scene Selector"

        scene = Scene(root, 200.0, 400.0)
        this.scene = scene

        // add all items and check already selected
        items.addAll(SceneRegistry.listOfActs())
        val selectedSceneNames = scenes.map { it.name }.toSet()
        items.filter { it.name in selectedSceneNames }.forEach { listView.checkModel.check(it) }

        // buttons
        val okButton = Button("OK")
        okButton.setOnAction {
            scenes.clear()
            listView.checkModel.checkedItems.forEach {
                scenes.add(it)
            }
            this.close()
        }

        val cancelButton = Button("Cancel")
        cancelButton.setOnAction {
            this.close()
        }

        val box = HBox(5.0)
        box.padding = Insets(10.0)
        box.add(okButton)
        box.add(cancelButton)

        root.center = listView
        root.bottom = box

        val jMetro = JMetro(Style.DARK)
        jMetro.scene = scene
    }

}