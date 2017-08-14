package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.view.IRenderer

class SceneManager(val tubes: List<Tube>) : IRenderer {
    val starScene = StarPatternScene(tubes)
    val leapMotionScene = LeapMotionScene(tubes)

    lateinit var activeScene: BaseScene

    override val timerTask: TimerTask
        get() = TimerTask(0, { render() })

    val timer = Timer(Sketch.instance)

    override fun setup() {
        starScene.setup()
        leapMotionScene.setup()

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
        timer.taskList.clear()
        activeScene = scene
        timer.addTask(activeScene.timerTask)
    }
}