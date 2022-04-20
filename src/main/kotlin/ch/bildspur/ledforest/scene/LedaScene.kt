package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.pose.Pose
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.pose.PoseLandmark
import processing.core.PVector

class LedaScene(
    project: Project, tubes: List<Tube>,
    val pulseScene: PulseScene,
    val poseProvider: PoseDataProvider
) : BaseInteractionScene("Leda Scene", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        pulseScene.setup()
    }

    override fun update() {
        if (!poseProvider.isRunning.get())
            return

        val config = project.leda

        // receive poses
        val poses = poseProvider.poses

        for (pose in poses) {
            for (collider in config.landmarkColliders) {
                checkCollision(pose, collider)
            }
        }

        pulseScene.update()
    }

    override fun stop() {
        pulseScene.stop()
    }

    override fun dispose() {
    }

    override val isInteracting: Boolean
        get() = poseProvider.poses.isNotEmpty()

    private fun checkCollision(pose: Pose, collider: LandmarkPulseCollider) {
        for (landmarkType in collider.triggeredBy.value) {
            val landmarkId = PoseLandmark.values().indexOf(landmarkType)
            val landmark = pose.keypoints[landmarkId]

            val score = landmark.t
            if (score < project.leda.landmarkMinScore.value) continue

            val relativeLandmarkPosition = PVector.sub(pose.nose, pose.keypoints[landmarkId])
            if (collider.checkCollision(relativeLandmarkPosition, landmarkType)) {
                collider.pulses.forEach {
                    project.pulseScene.pulses.add(it.spawn())
                }
            }
        }
    }
}