package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.view.IRenderer

class SceneManager(val project: Project, val tubes: List<Tube>) : IRenderer {
    val starScene = StarPatternScene(tubes)
    val leapMotionScene = LeapMotionScene(tubes)
    val blackScene = BlackScene(tubes)
    val strobeScene = StrobeScene(tubes)

    var activeScene: BaseScene = blackScene

    val easterEggGrabStrength = 0.8f

    private val task = TimerTask(0, { render() }, "SceneManager")

    override val timerTask: TimerTask
        get() = task

    val timer = Timer()

    override fun setup() {
        timer.setup()
        initScene(starScene)
    }

    override fun render() {
        // check if hand is detected
        if (activeScene != leapMotionScene && leapMotionScene.isLeapAvailable() && project.isSceneManager.value)
            initScene(leapMotionScene)

        if (activeScene != starScene && !leapMotionScene.isLeapAvailable() && project.isSceneManager.value)
            initScene(starScene)

        if (activeScene != blackScene && !project.isSceneManager.value)
            initScene(blackScene)

        try {
            if (activeScene != strobeScene
                    && project.isStrobeEnabled.value
                    && leapMotionScene.isLeapAvailable()
                    && leapMotionScene.leap.hands.map { it.grabStrength.value > easterEggGrabStrength }.any())
                initScene(strobeScene)
        } catch (ex: Exception) {
            println("NPE: 05")
        }

        timer.update()
    }

    override fun dispose() {
        starScene.dispose()
        leapMotionScene.dispose()
        blackScene.dispose()
        strobeScene.dispose()
    }

    internal fun initScene(scene: BaseScene) {
        activeScene.stop()
        timer.taskList.remove(activeScene.timerTask)

        activeScene = scene
        activeScene.setup()
        timer.addTask(activeScene.timerTask)
    }
}