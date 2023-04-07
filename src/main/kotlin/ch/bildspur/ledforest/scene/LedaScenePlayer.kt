package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.forEachLED

class LedaScenePlayer(project: Project, tubes: List<Tube>, vararg val scenes: BaseScene) :
    BaseScene("LedaScenePlayer", project, tubes) {
    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    var activeScene: BaseScene = scenes[0]

    override fun setup() {
        // set all tube leds to black
        project.tubes.forEachLED {
            it.color.fade(ColorMode.color(0), 0.05f)
        }

        activeScene.setup()
    }

    override fun update() {
        activeScene.update()
    }

    override fun stop() {
        activeScene.stop()
    }

    override fun dispose() {
    }

    private fun switchScene(scene: BaseScene) {
        activeScene.stop()
        activeScene = scene
        activeScene.setup()
    }
}