package ch.bildspur.ledforest.model.preset

import ch.bildspur.ledforest.configuration.ConfigurationController
import ch.bildspur.ledforest.ui.properties.SeparatorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.model.SelectableDataModel
import ch.bildspur.ui.fx.FXPropertyRegistry
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.PropertyReader
import ch.bildspur.ui.properties.SelectableListParameter
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import jfxtras.styles.jmetro.FlatTextInputDialog
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style


abstract class PresetManager() {
    companion object {
        private const val presetsName = "presets"
    }

    @Expose
    @SerializedName(presetsName)
    @SelectableListParameter("Presets")
    var presets = SelectableDataModel(mutableListOf<Preset>())

    @ActionParameter("Preset", "Create", invokesChange = false, uiThread = true)
    private val addPreset = {
        val jMetro = JMetro(Style.DARK)
        val dialog = FlatTextInputDialog("")
        jMetro.scene = dialog.dialogPane.scene

        dialog.title = "Create new Preset"
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

    @ActionParameter("Preset", "Update", invokesChange = false, uiThread = true)
    private val updatePreset = {
        if (presets.selectedIndex < 0) {
            addPreset()
        } else {
            presets.selectedItem.data = createPresetJson()
            presets.selectedItem = presets.selectedItem
        }
    }

    @ActionParameter("Preset", "Load", invokesChange = false, uiThread = true)
    private val loadPreset = {
        if (presets.selectedIndex >= 0) {
            applyPresetJson()
        }
    }

    @ActionParameter("Preset", "Delete", invokesChange = false, uiThread = true)
    private val deletePreset = {
        if (presets.selectedIndex >= 0) {
            presets.remove(presets.selectedItem)
        }
    }

    @SeparatorParameter()
    private val separator = Any()

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