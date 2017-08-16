package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.view.IRenderer

class SceneManager(val tubes: List<Tube>) : IRenderer {
    val starScene = StarPatternScene(tubes)
    val leapMotionScene = LeapMotionScene(tubes)

    var activeScene: BaseScene = starScene

    override val timerTask: TimerTask
        get() = TimerTask(0, { render() })

    val timer = Timer()

    override fun setup() {
        timer.setup()
        initScene(starScene)
    }

    override fun render() {
        timer.update()
    }

    override fun dispose() {
        starScene.dispose()
        leapMotionScene.dispose()
    }

    internal fun initScene(scene: BaseScene) {
        activeScene.stop()
        timer.taskList.remove(activeScene.timerTask)

        activeScene = scene
        activeScene.setup()
        timer.addTask(activeScene.timerTask)
    }
}