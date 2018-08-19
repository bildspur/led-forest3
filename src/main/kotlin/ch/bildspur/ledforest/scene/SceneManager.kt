package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.view.IRenderer

class SceneManager(val sketch: Sketch, val project: Project, val tubes: List<Tube>) : IRenderer {
    val starScene = StarPatternScene(project, tubes)
    val leapMotionScene = LeapMotionScene(project, tubes, sketch.leapMotion)
    val realSenseScene = RealSenseScene(project, tubes, sketch.realSense)
    val blackScene = BlackScene(project, tubes)
    val strobeScene = StrobeScene(project, tubes)

    var activeScene: BaseScene = blackScene

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
        if (activeScene != leapMotionScene
                && leapMotionScene.isInteracting
                && project.isSceneManagerEnabled.value
                && project.interaction.isLeapInteractionEnabled.value)
            initScene(leapMotionScene)

        if (activeScene != realSenseScene
                && realSenseScene.isInteracting
                && project.isSceneManagerEnabled.value
                && project.interaction.isRealSenseInteractionEnabled.value)
            initScene(realSenseScene)

        if (activeScene != starScene
                && !leapMotionScene.isInteracting
                && !realSenseScene.isInteracting
                && project.isSceneManagerEnabled.value)
            initScene(starScene)

        if (activeScene != blackScene && !project.isSceneManagerEnabled.value)
            initScene(blackScene)

        try {
            if (activeScene != strobeScene
                    && project.leapInteraction.isStrobeEnabled.value
                    && leapMotionScene.isInteracting
                    && leapMotionScene.leap.hands.map { it.grabStrength.value >= project.leapInteraction.strobeThreshold.value }.contains(true))
                initScene(strobeScene)
        } catch (ex: Exception) {
            println("NPE: 05")
        }

        project.activeScene.value = activeScene.name

        timer.update()
    }

    override fun dispose() {
        starScene.dispose()
        leapMotionScene.dispose()
        blackScene.dispose()
        strobeScene.dispose()
        realSenseScene.dispose()
    }

    internal fun initScene(scene: BaseScene) {
        activeScene.stop()
        timer.taskList.remove(activeScene.timerTask)

        activeScene = scene
        activeScene.setup()
        timer.addTask(activeScene.timerTask)
    }
}