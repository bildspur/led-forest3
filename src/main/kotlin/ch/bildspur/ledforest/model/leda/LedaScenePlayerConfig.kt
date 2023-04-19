package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.configuration.PostProcessable
import ch.bildspur.ledforest.configuration.sync.ApiExposed
import ch.bildspur.ledforest.model.scene.SceneLink
import ch.bildspur.ledforest.scene.BaseScene
import ch.bildspur.ledforest.ui.SceneSelectorDialog
import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.model.SelectableDataModel
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import javafx.application.Platform
import javafx.stage.Modality

class LedaScenePlayerConfig : PostProcessable {
    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @Expose
    @ApiExposed("scene_index")
    @NumberParameter("Scene Index")
    var sceneIndex = DataModel(0)

    @Expose
    @SelectableListParameter("Scenes")
    var scenes = SelectableDataModel<SceneLink>()

    fun getActiveScene(): BaseScene? {
        if (sceneIndex.value < scenes.count()) {
            val scene = scenes.value[sceneIndex.value].resolve()
            if (scene != null)
                return scene
        }

        return null
    }

    @ActionParameter("Scenes", "Edit")
    private val showEditScenesMenu = {
        Platform.runLater {
            val dialog = SceneSelectorDialog(scenes)
            dialog.initModality(Modality.APPLICATION_MODAL)
            dialog.show()
        }
    }

    @Expose
    @ApiExposed("auto_play")
    @BooleanParameter("Is Playing")
    var isPlaying = DataModel(false)

    @Expose
    @RangeSliderParameter("Playtime (min)", minValue = 0.0, maxValue = 120.0, majorTick = 1.0)
    var playtimeInMinutes = DataModel(NumberRange(0.5, 1.0))

    @Expose
    @BooleanParameter("Random Order")
    var randomOrder = DataModel(false)

    private fun setupEventHandlers() {
        sceneIndex.onChanged += {
            if (0 <= it && it < scenes.size) {
                Platform.runLater {
                    scenes.selectedIndex = it
                }
            } else {
                sceneIndex.value = 0
            }
        }

        scenes.onChanged += {
            if (scenes.selectedIndex >= 0)
                sceneIndex.value = scenes.selectedIndex
        }
    }

    override fun gsonPostProcess() {
        setupEventHandlers()
    }
}