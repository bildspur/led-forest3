package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.ExtendedRandom
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.timer.ElapsedTimer
import kotlin.math.max
import kotlin.math.roundToLong

class LedaScenePlayer(project: Project, tubes: List<Tube>, vararg val scenes: BaseScene) :
    BaseScene("LedaScenePlayer", project, tubes) {
    private val task = TimerTask(10, { update() })
    private val random = ExtendedRandom()

    override val timerTask: TimerTask
        get() = task

    var activeScene: BaseScene = scenes[project.ledaScenePlayer.sceneIndex.value]

    private val timer = ElapsedTimer()

    override fun setup() {
        project.ledaScenePlayer.scenes.clear()
        scenes.forEach {
            project.ledaScenePlayer.scenes.add(it)
        }
        project.ledaScenePlayer.scenes.selectedIndex = max(0, project.ledaScenePlayer.sceneIndex.value)

        // set all tube leds to black
        project.tubes.forEachLED {
            it.color.fade(ColorMode.color(0), 0.05f)
        }

        activeScene.setup()
        resetPlaybackTimer()
    }

    override fun update() {
        activeScene.update()

        if (project.ledaScenePlayer.isPlaying.value) {
            if (timer.elapsed()) {
                playNextScene()
            }
        } else {
            if (activeScene != project.ledaScenePlayer.scenes.selectedItem) {
                switchScene(scenes[project.ledaScenePlayer.sceneIndex.value])
            }
        }
    }

    override fun stop() {
        activeScene.stop()
    }

    override fun dispose() {
    }

    private fun playNextScene() {
        val nextIndex = if (project.ledaScenePlayer.randomOrder.value) {
            random.randomInt(0, scenes.count())
        } else {
            (project.ledaScenePlayer.sceneIndex.value + 1) % scenes.count()
        }

        val nextScene = scenes[nextIndex]
        println("Switching to scene: ${nextScene.name}")
        project.ledaScenePlayer.sceneIndex.value = nextIndex
        switchScene(nextScene)

        resetPlaybackTimer()
    }

    private fun resetPlaybackTimer() {
        val playTimeRange = project.ledaScenePlayer.playtimeInMinutes.value
        val playTime = random.randomFloat(playTimeRange)
        val playTimeInMs = (playTime * 60 * 1000).roundToLong()

        timer.duration = playTimeInMs
        timer.reset()
    }

    private fun switchScene(scene: BaseScene) {
        activeScene.stop()
        activeScene = scene
        activeScene.setup()
    }
}