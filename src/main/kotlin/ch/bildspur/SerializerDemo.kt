package ch.bildspur

import ch.bildspur.ledforest.configuration.ConfigurationController
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.scene.BaseScene
import ch.bildspur.ledforest.scene.SceneRegistry
import ch.bildspur.ledforest.scene.StarPatternScene
import ch.bildspur.ledforest.scene.pulse.PulseScene
import ch.bildspur.model.DataModel
import ch.bildspur.model.SelectableDataModel
import ch.bildspur.ui.properties.SelectableListParameter
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.annotations.Expose

class Config {
    @Expose
    @SelectableListParameter("Scenes")
    var scenes = SelectableDataModel<BaseScene>()

    @Expose
    var scene = DataModel<BaseScene>(StarPatternScene(Project(), emptyList()))
}

fun main() {
    val config = Config()
    config.scenes.add(StarPatternScene(Project(), emptyList()))
    config.scenes.add(PulseScene(Project(), emptyList()))

    val configurator = ConfigurationController()
    val result = configurator.gson.toJson(config)

    SceneRegistry.registerScene(StarPatternScene(Project(), emptyList()))

    println(result)

    val cfg = configurator.gson.fromJson<Config>(result)
    cfg.scenes.forEach {
        println(it.name)
    }
}