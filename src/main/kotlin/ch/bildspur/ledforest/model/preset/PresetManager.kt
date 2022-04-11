package ch.bildspur.ledforest.model.preset

import ch.bildspur.ledforest.configuration.ConfigurationController
import ch.bildspur.model.SelectableDataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.SelectableListParameter
import com.google.gson.InstanceCreator
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javafx.scene.control.TextInputDialog
import java.lang.reflect.Type


abstract class PresetManager() {
    companion object {
        private const val presetsName = "presets"
    }

    @Expose
    @SerializedName(presetsName)
    @SelectableListParameter("Presets")
    var presets = SelectableDataModel(mutableListOf<Preset>())

    @ActionParameter("Preset", "Create", uiThread = true)
    private val addPreset = {
        val dialog = TextInputDialog("")
        dialog.title = "Create new Preset"
        dialog.headerText = "Create new Preset"
        dialog.contentText = "Please enter the name:"

        val result = dialog.showAndWait()

        result.ifPresent { name: String ->
            val preset = Preset(name, createPresetJson())
            presets.add(preset)
            presets.selectedItem = preset
        }
    }

    @ActionParameter("Preset", "Load", uiThread = true)
    private val loadPreset = {
        if (presets.selectedIndex >= 0) {
            val jsonString = presets.selectedItem.data

            val gson = ConfigurationController().gsonBuilder
                    .registerTypeAdapter(this::class.java, InstanceCreator<Any> { type: Type? -> this } as InstanceCreator<*>)
                    .create()
            gson.fromJson(jsonString, this.javaClass)
        }
    }

    @ActionParameter("Preset", "Save", uiThread = true)
    private val savePreset = {
        if (presets.selectedIndex < 0) {
            addPreset()
        } else {
            presets.selectedItem.data = createPresetJson()
        }
    }

    @ActionParameter("Preset", "Delete", uiThread = true)
    private val deletePreset = {
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
}