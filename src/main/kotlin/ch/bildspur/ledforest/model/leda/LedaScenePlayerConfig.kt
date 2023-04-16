package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.configuration.sync.ApiExposed
import ch.bildspur.ledforest.scene.BaseScene
import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.model.SelectableDataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.RangeSliderParameter
import ch.bildspur.ui.properties.SelectableListParameter
import com.google.gson.annotations.Expose
import javafx.application.Platform

class LedaScenePlayerConfig {
    @Expose
    @BooleanParameter("Enabled")
    var enabled = DataModel(false)

    @ApiExposed("scene_index")
    @NumberParameter("Scene Index")
    var sceneIndex = DataModel(0)

    @SelectableListParameter("Scenes")
    var scenes = SelectableDataModel<BaseScene>()

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

    init {
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
}