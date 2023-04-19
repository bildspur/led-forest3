package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.preset.PresetManager

class PresetScene(val scene: BaseScene, val presetName: String, val sceneConfig: PresetManager) :
    BaseScene("${scene.name} $presetName", scene.project, scene.tubes) {
    override val timerTask: TimerTask
        get() = scene.timerTask

    override fun setup() {
        applyPreset()
        scene.setup()
    }

    override fun update() {
        scene.update()
    }

    override fun stop() {
        scene.stop()
    }

    override fun dispose() {
        scene.dispose()
    }

    fun applyPreset() {
        val preset = sceneConfig.presets.filter { it.name == presetName }.firstOrNull()
        if (preset != null) {
            sceneConfig.applyPreset(preset)
        } else {
            println("WARNING: Could not find preset ${presetName} for ${scene.name} config.")
        }
    }
}