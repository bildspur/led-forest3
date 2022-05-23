package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.LEDRing
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.pose.Pose
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.Debouncer
import processing.core.PVector

class LedaScene(
    project: Project, tubes: List<Tube>,
    val pulseScene: PulseScene,
    val poseProvider: PoseDataProvider
) : BaseInteractionScene("Leda Scene", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    private var ledRing: LEDRing? = null
    private var poseDetected = Debouncer(500, false)

    override fun setup() {
        pulseScene.setup()

        // try to find led ring
        ledRing = project.spatialLightElements.first { it is LEDRing } as LEDRing
    }

    override fun update() {
        if (!poseProvider.isRunning.get())
            return

        val config = project.leda

        // receive poses
        val poses = poseProvider.poses.take(config.interactorLimit.value)

        for (pose in poses) {
            for (collider in config.landmarkColliders) {
                checkCollision(pose, collider)
            }

            // todo: implement realtime pose interaction
            // todo: implement pose-classification
        }

        poseDetected.update(poses.isNotEmpty())

        // check if led should be turned on
        if(poseDetected.currentValue) {
            ledRing?.leds?.forEach {
                it.color.fade(ColorMode.color(255),0.2f)
            }
        } else {
            ledRing?.leds?.forEach {
                it.color.fade(ColorMode.color(0),0.2f)
            }
        }

        pulseScene.update()
    }

    override fun stop() {
        pulseScene.stop()

        ledRing?.leds?.forEach {
            it.color.fade(ColorMode.color(0),0.2f)
        }
    }

    override fun dispose() {
    }

    override val isInteracting: Boolean
        get() = poseProvider.poses.isNotEmpty()

    private fun checkCollision(pose: Pose, collider: LandmarkPulseCollider) {
        val origin = project.leda.triggerOrigin.value

        for (landmarkType in collider.triggeredBy.value) {
            val landmarkId = PoseLandmark.values().indexOf(landmarkType)
            val landmark = pose.keypoints[landmarkId]

            val score = landmark.score
            if (score < project.leda.landmarkMinScore.value) continue

            val relativeLandmarkPosition = PVector.sub(origin, pose.keypoints[landmarkId])
            if (collider.checkCollision(relativeLandmarkPosition, landmarkType)) {
                collider.pulses.forEach {
                    project.pulseScene.pulses.add(it.spawn())
                }
            }
        }
    }
}