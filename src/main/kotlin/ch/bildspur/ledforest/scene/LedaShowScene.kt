package ch.bildspur.ledforest.scene

import ch.bildspur.color.HSV
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.forEachLED
import kotlin.math.roundToInt

class LedaShowScene(project: Project, tubes: List<Tube>) : BaseScene("Leda Show", project, tubes) {
    private val task = TimerTask(1, { update() })

    private var emptyScene = PassthroughScene(project, tubes)
    private var showScene: BaseScene = emptyScene

    init {
        project.videoScene.onVideoEnded += {
            project.leda.ledaShow.hasShowEnded.value = true
        }
    }

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        val config = project.leda.ledaShow

        // reset show trigger flags
        config.showTrigger.value = false
        config.hasShowEnded.value = false
        showScene = emptyScene

        // find relevant video scene and play it back
        val act = SceneRegistry.listOfActs().firstOrNull { it.name.endsWith(config.videoName.value) }

        if (act == null) {
            println("Could not find show scene ${config.videoName.value}.")
            config.hasShowEnded.value = true
        } else {
            showScene = act
        }

        showScene.setup()

        // change video scene parameters
        val currentTime = System.currentTimeMillis()
        println("Current: ${currentTime}")
        println("Current: ${config.startTimeStamp.value.toEpochMilliseconds()}")
        println("Time Miss: ${config.timeUntilPlayback} ms")
        project.videoScene.videoStartTime.value = config.startTimeStamp.value.toEpochMilliseconds()
    }

    override fun update() {
        val config = project.leda.ledaShow

        if (config.timeUntilPlayback > 0) {
            val brightnessFactor = config.timeUntilPlayback / config.totalWaitTime.toFloat()

            val hsv = project.leda.ledaShow.transitionColor.value.toHSV()
            val color = HSV(hsv.h, hsv.s, (hsv.v * brightnessFactor).roundToInt()).toRGB().toPackedInt()

            tubes.forEachLED { it.color.set(color) }
            return
        }

        showScene.update()
    }

    override fun stop() {
        showScene.stop()
    }

    override fun dispose() {
    }


}