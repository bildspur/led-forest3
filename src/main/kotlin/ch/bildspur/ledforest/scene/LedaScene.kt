package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.pose.PoseDataProvider

class LedaScene(project: Project, tubes: List<Tube>,
                val pulseScene: PulseScene,
                val poseProvider: PoseDataProvider)
    : BaseInteractionScene("Leda Scene", project, tubes) {

    private val task = TimerTask(0, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        pulseScene.setup()
    }

    override fun update() {
        if (!poseProvider.isRunning.get())
            return
        val config = project.poseInteraction

        // receive poses
        val poses = poseProvider.poses

        // check if triggers have been hit


        pulseScene.update()
    }

    override fun stop() {
        pulseScene.stop()
    }

    override fun dispose() {
    }

    override val isInteracting: Boolean
        get() = poseProvider.poses.isNotEmpty()
}