package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.light.LEDRing
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.pose.Pose
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.statemachine.*
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.Debouncer
import ch.bildspur.ledforest.util.forEachLED
import processing.core.PVector

class LedaScene(
    project: Project, tubes: List<Tube>,
    val idleScene: BaseScene,
    val pulseScene: PulseScene,
    val poseScene: PoseScene,
    val poseProvider: PoseDataProvider
) : BaseInteractionScene("Leda Scene", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    // scene variables
    private var ledRing: LEDRing? = null
    private var poseDetected = Debouncer(500, false)
    private var poses = emptyList<Pose>()

    // states
    val idleState = SceneState(idleScene)
    val poseState = SceneState(poseScene)
    val pulseState = SceneState(pulseScene)

    val offState = TimedState("Off", 1000L, idleState)
    val welcomeState = TimedState("Welcome", 1000L, poseState)

    val stateMachine = StateMachine(offState)

    init {
        // define state behaviour
        offState.onUpdate = {
            fadeLEDRing(ColorMode.color(0))
            tubes.forEachLED { it.color.fade(ColorMode.color(0), 0.01f) }
            StateResult()
        }

        idleState.onUpdate = {
            if (poseDetected.currentValue) StateResult(welcomeState)
            else StateResult()
        }

        poseState.onUpdate = {
            // check for collisions
            var hasCollision = false
            for (pose in poses) {
                for (collider in project.leda.landmarkColliders) {
                    if (checkCollision(pose, collider)) {
                        hasCollision = true
                    }
                }
            }

            if (!poseDetected.currentValue) StateResult(offState)
            else if (hasCollision) StateResult(pulseState)
            else StateResult()
        }

        pulseState.onUpdate = {
            if (project.pulseScene.pulses.isEmpty()) StateResult(poseState)
            else StateResult()
        }
    }

    override fun setup() {
        // try to find led ring
        ledRing = project.spatialLightElements.first { it is LEDRing } as LEDRing

        stateMachine.setup()
    }

    override fun update() {
        if (!poseProvider.isRunning.get())
            return

        // receive poses
        poses = poseProvider.poses.take(project.leda.interactorLimit.value)
        poseDetected.update(poses.isNotEmpty())

        stateMachine.update()
    }

    override fun stop() {
        fadeLEDRing(ColorMode.color(0))
        stateMachine.release()
    }

    override fun dispose() {
    }

    override val isInteracting: Boolean
        get() = poseProvider.poses.isNotEmpty()

    private fun fadeLEDRing(target: Int, easing: Float = 0.1f) {
        ledRing?.leds?.forEach {
            it.color.fade(target, easing)
        }
    }

    private fun checkCollision(pose: Pose, collider: LandmarkPulseCollider): Boolean {
        val origin = project.leda.triggerOrigin.value

        var hasCollision = false

        for (landmarkType in collider.triggeredBy.value) {
            val landmarkId = PoseLandmark.values().indexOf(landmarkType)
            val landmark = pose.keypoints[landmarkId]

            val score = landmark.score
            if (score < project.leda.landmarkMinScore.value) continue

            val relativeLandmarkPosition = PVector.sub(origin, pose.keypoints[landmarkId])
            if (collider.checkCollision(relativeLandmarkPosition, landmarkType)) {
                hasCollision = true
                collider.pulses.forEach {
                    project.pulseScene.pulses.add(it.spawn())
                }
            }
        }

        return hasCollision
    }
}