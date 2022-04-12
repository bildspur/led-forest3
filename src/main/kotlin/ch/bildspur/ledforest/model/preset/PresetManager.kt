package ch.bildspur.ledforest.model.preset

import ch.bildspur.ledforest.configuration.ConfigurationController
import ch.bildspur.ledforest.ui.properties.CustomUIParameter
import ch.bildspur.ledforest.ui.properties.SeparatorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.model.SelectableDataModel
import ch.bildspur.ui.fx.BaseFXFieldProperty
import ch.bildspur.ui.fx.FXPropertyRegistry
import ch.bildspur.ui.properties.PropertyReader
import ch.bildspur.ui.properties.SelectableListParameter
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import jfxtras.styles.jmetro.FlatTextInputDialog
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import org.kordamp.ikonli.javafx.FontIcon


abstract class PresetManager {
    companion object {
        private const val presetsName = "presets"
    }

    @Expose
    @SerializedName(presetsName)
    @SelectableListParameter("Presets")
    var presets = SelectableDataModel(mutableListOf<Preset>())

    @CustomUIParameter
    private var presetManagerUI = { property: BaseFXFieldProperty ->
        property.alignment = Pos.CENTER_RIGHT
        property.spacing = 3.0

        val createPresetButton = Button("")
        val removePresetButton = Button("")
        val updatePresetButton = Button("")
        val applyPresetButton = Button("")

        createPresetButton.tooltip = Tooltip("Create new Preset")
        removePresetButton.tooltip = Tooltip("Remove selected Preset")
        updatePresetButton.tooltip = Tooltip("Update select Preset")
        applyPresetButton.tooltip = Tooltip("Apply selected Preset")

        createPresetButton.setOnAction { createPreset() }
        updatePresetButton.setOnAction { updatePreset() }
        applyPresetButton.setOnAction { applyPreset() }
        removePresetButton.setOnAction { removePreset() }

        createPresetButton.graphic = FontIcon("bi-file-earmark-plus")
        updatePresetButton.graphic = FontIcon("bi-file-arrow-up")
        applyPresetButton.graphic = FontIcon("bi-file-arrow-down-fill")
        removePresetButton.graphic = FontIcon("bi-file-earmark-x")

        property.children.addAll(
                createPresetButton,
                updatePresetButton,
                applyPresetButton,
                removePresetButton
        )
    }

    @SeparatorParameter()
    private val separator = Any()

    private fun createPreset() {
        val jMetro = JMetro(Style.DARK)
        val dialog = FlatTextInputDialog("")
        jMetro.scene = dialog.dialogPane.scene

        dialog.title = "Preset"
        dialog.headerText = "Create new Preset"
        dialog.contentText = "Please enter the name:"

        val result = dialog.showAndWait()

        result.ifPresent { name: String ->
            if (name.isBlank()) return@ifPresent

            val preset = Preset(name, createPresetJson())
            presets.add(preset)
            presets.selectedItem = preset
        }
    }

    private fun updatePreset() {
        if (presets.selectedIndex < 0) {
            createPreset()
        } else {
            presets.selectedItem.data = createPresetJson()
            presets.selectedItem = presets.selectedItem
        }
    }

    private fun applyPreset() {
        if (presets.selectedIndex >= 0) {
            applyPresetJson()
        }
    }

    private fun removePreset() {
        if (presets.selectedIndex >= 0) {
            presets.remove(presets.selectedItem)
        }
    }

    private fun createPresetJson(): String {
        val configController = ConfigurationController()
        val json = configController.gson.toJsonTree(this) as JsonObject

        // remove presetManager fields
        json.remove(presetsName)
        return json.toString()
    }

    private fun applyPresetJson() {
        val jsonString = presets.selectedItem.data

        val gson = ConfigurationController().gsonBuilder.create()
        val obj = gson.fromJson(jsonString, this.javaClass)
        transferDataModelValues(obj, this)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any, K : Any> transferDataModelValues(a: T, b: K) {
        val propertyReader = PropertyReader(FXPropertyRegistry.properties)

        val aProperties = propertyReader.readPropertyAnnotations(a)
        val bProperties = propertyReader.readPropertyAnnotations(b)

        val bPropertiesLookupTable = bProperties.associateBy { it.field.name }.toMutableMap()
        bPropertiesLookupTable.remove(presetsName)

        val dataModelClassName = DataModel::class.qualifiedName
        for (aProperty in aProperties) {
            val bProperty = bPropertiesLookupTable[aProperty.field.name] ?: continue
            if (aProperty.field.type != bProperty.field.type) continue

            if (!(aProperty.field.type.name == dataModelClassName && bProperty.field.type.name == dataModelClassName)) continue

            val aDataModel = aProperty.field.get(a) as DataModel<Any>
            val bDataModel = bProperty.field.get(b) as DataModel<Any>

            bDataModel.value = aDataModel.value
        }
    }
}