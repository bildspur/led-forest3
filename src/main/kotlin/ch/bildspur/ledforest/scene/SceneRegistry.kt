package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.model.preset.PresetManager

data class RegisteredScene(val scene: BaseScene, val config: PresetManager?)


object SceneRegistry {
    val scenes = mutableMapOf<String, RegisteredScene>()

    fun registerScene(scene: BaseScene, config: PresetManager? = null) {
        scenes[scene.name] = RegisteredScene(scene, config)
    }

    fun clear() {
        scenes.clear()
    }

    operator fun get(name: String): RegisteredScene? {
        return scenes[name]
    }

    fun listOfActs(): List<BaseScene> {
        val acts = mutableListOf<BaseScene>()

        scenes.forEach {
            val registeredScene = it.value
            acts.add(registeredScene.scene)

            if (registeredScene.config != null) {
                registeredScene.config.presets.forEach { p ->
                    val presetScene = PresetScene(registeredScene.scene, p.name, registeredScene.config)
                    acts.add(presetScene)
                }
            }
        }

        return acts
    }
}