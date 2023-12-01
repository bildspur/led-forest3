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

class LedaScenePlayer(project: Project, tubes: List<Tube>) :
    BaseScene("LedaScenePlayer", project, tubes) {
    private val task = TimerTask(10, { update() })
    private val random = ExtendedRandom()

    override val timerTask: TimerTask
        get() = task

    var activeScene: BaseScene? = project.ledaScenePlayer.getActiveScene()

    private val timer = ElapsedTimer()

    override fun setup() {
        project.ledaScenePlayer.scenes.selectedIndex = max(0, project.ledaScenePlayer.sceneIndex.value)

        // set all tube leds to black
        project.tubes.forEachLED {
            it.color.fade(ColorMode.color(0), 0.05f)
        }

        activeScene?.setup()
        resetPlaybackTimer()
    }

    override fun update() {
        activeScene?.update()

        if (project.ledaScenePlayer.isPlaying.value) {
            val noSceneSelected = activeScene == null
            if (timer.elapsed() || noSceneSelected) {
                playNextScene(noSceneSelected)
            }
        } else {
            val newActive = project.ledaScenePlayer.getActiveScene()
            if (newActive != null && newActive != activeScene) {
                switchScene(newActive)
            }
        }
    }

    override fun stop() {
        activeScene?.stop()
    }

    override fun dispose() {
    }

    private fun playNextScene(keepSceneIndex: Boolean = false) {
        if (project.ledaScenePlayer.scenes.isEmpty()) return

        var nextIndex = if (keepSceneIndex) {
            project.ledaScenePlayer.sceneIndex.value
        } else if (project.ledaScenePlayer.randomOrder.value) {
            // randomInt returns a value including lower and upper bound
            random.randomInt(0, project.ledaScenePlayer.scenes.count() - 1)
        } else {
            (project.ledaScenePlayer.sceneIndex.value + 1) % project.ledaScenePlayer.scenes.count()
        }

        // always limit next index to not run into errors
        nextIndex %= project.ledaScenePlayer.scenes.value.size

        val nextScene = project.ledaScenePlayer.scenes.value[nextIndex].resolve() ?: return

        println("Switching to scene: ${nextScene.name} ($nextIndex)")
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
        activeScene?.stop()
        activeScene = scene
        activeScene?.setup()
    }
}