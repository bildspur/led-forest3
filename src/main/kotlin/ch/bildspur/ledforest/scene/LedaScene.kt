package ch.bildspur.ledforest.scene

import ch.bildspur.color.RGB
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.light.LEDRing
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
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
) : BaseInteractionScene("Leda", project, tubes) {

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

    val offState = TimedState("Off", 250L, idleState)
    val welcomeState = TimedState("Welcome", 2000L, poseState)

    val stateMachine = StateMachine(offState)

    init {
        // define state behaviour
        offState.onUpdate = {
            fadeLEDRing(ColorMode.color(0))
            tubes.forEachLED { it.color.fade(ColorMode.color(0), 0.01f) }
            StateResult()
        }

        idleState.onUpdate = {
            if (project.leda.enabledInteraction.value && poseDetected.currentValue) StateResult(welcomeState)
            else StateResult()
        }

        welcomeState.onActivate = {
            pulseScene.setup()

            // todo: implement as settings
            val pulse = Pulse()
            pulse.duration.value = 2000f
            pulse.distance.value = 6.5f
            pulse.color.value = RGB("#ffffff")
            pulse.width.value = 2f
            pulse.expansionCurve.value = EasingMethod.EaseOutQuad

            project.pulseScene.pulses.add(pulse.spawn())
        }

        welcomeState.onUpdate = {
            pulseScene.update()
            StateResult()
        }

        welcomeState.onDeactivate = {
            pulseScene.stop()
        }

        poseState.onUpdate = {
            // check for collisions
            var hasCollision = false
            if (project.leda.enabledCollider.value) {
                for (pose in poses) {
                    for (collider in project.leda.landmarkColliders) {
                        if (checkCollision(pose, collider)) {
                            hasCollision = true
                        }
                    }
                }
            }

            if (!project.leda.enabledInteraction.value) StateResult(offState)
            else if (!poseDetected.currentValue) StateResult(offState)
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
        project.leda.currentState.value = stateMachine.activeStateName

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