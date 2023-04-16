package ch.bildspur.ledforest.scene

import ch.bildspur.color.RGB
import ch.bildspur.ledforest.animator.LightAnimator
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.firelog.FireLog
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.model.leda.CollisionCandidate
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.light.LEDRing
import ch.bildspur.ledforest.model.light.LEDSpot
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.Pose
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.scene.pulse.PulseEmitterScene
import ch.bildspur.ledforest.scene.pulse.PulseScene
import ch.bildspur.ledforest.statemachine.SceneState
import ch.bildspur.ledforest.statemachine.StateMachine
import ch.bildspur.ledforest.statemachine.StateResult
import ch.bildspur.ledforest.statemachine.TimedState
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.Debouncer
import ch.bildspur.ledforest.util.forEachLED
import processing.core.PVector
import kotlin.math.max

class LedaScene(
    project: Project, tubes: List<Tube>,
    val idleScene: BaseScene,
    val pulseScene: PulseScene,
    val poseScene: PoseScene,
    val scenePlayer: LedaScenePlayer,
    val poseProvider: PoseDataProvider
) : BaseInteractionScene("Leda", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
        get() = task

    // scene variables
    private var ledRing: LEDRing? = null
    private val ledRingAnimator = LightAnimator()

    private var poseDetected = Debouncer(500, false)
    private var poses = emptyList<Pose>()

    private var randomPulseScene = PulseEmitterScene(pulseScene, project, tubes, "Random Pulse")

    // states
    val idleState = SceneState(idleScene)
    val poseState = SceneState(poseScene)
    val pulseState = SceneState(pulseScene)
    val scenePlayerState = SceneState(scenePlayer)
    val pulseInteractionState = SceneState(pulseScene)
    val randomPulseState = SceneState(randomPulseScene)

    val offState = TimedState("Off", 250L, idleState)
    val welcomeState = TimedState("Welcome", 1000L, poseState)

    val stateMachine = StateMachine(offState)

    init {
        stateMachine.onStateChanged += {
            FireLog.log(eventType = "state-switch", params = buildMap { put("name", it.name) })
        }

        // define state behaviour
        offState.onUpdate = {
            ledRingAnimator.fadeAll(ColorMode.color(0))
            tubes.forEachLED { it.color.fade(ColorMode.color(0), 0.01f) }
            StateResult()
        }

        idleState.onUpdate = {
            ledRingAnimator.fadeAll(ColorMode.color(0, 0, 80))
            if (project.leda.enabledInteraction.value && poseDetected.currentValue) StateResult(welcomeState)
            else if (project.ledaScenePlayer.enabled.value) StateResult(scenePlayerState)
            else if (project.leda.enableRandomPulses.value) StateResult(randomPulseState)
            else StateResult()
        }

        scenePlayerState.onUpdate = {
            if (project.ledaScenePlayer.enabled.value) StateResult()
            else StateResult(idleState)
        }

        welcomeState.onActivate = {
            ledRingAnimator.fadeAll(ColorMode.color(0, 0, 100))

            // check which scene should follow
            welcomeState.nextState = if (project.leda.colliderSceneOnly.value) {
                pulseInteractionState
            } else {
                poseState
            }

            pulseScene.setup()

            // disable old pulses
            val ts = System.currentTimeMillis()
            project.pulseScene.pulses.forEach {
                it.duration.value = max(500f, it.getProgress(ts) * it.duration.value)
            }

            // send welcome pulse
            // todo: implement as settings
            val pulse = Pulse()
            pulse.duration.value = 1000f
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

        // direct interaction scene
        poseState.onActivate = {
            ledRingAnimator.fadeAll(ColorMode.color(0, 0, 80))
        }

        poseState.onUpdate = {
            // check for collisions
            val hasCollision = updateCollisions()

            if (!project.leda.enabledInteraction.value) StateResult(offState)
            else if (project.ledaScenePlayer.enabled.value) StateResult(scenePlayerState)
            else if (!poseDetected.currentValue) StateResult(offState)
            else if (hasCollision) StateResult(pulseState)
            else StateResult()
        }

        pulseState.onUpdate = {
            if (project.pulseScene.pulses.isEmpty()) StateResult(poseState)
            else StateResult()
        }

        // collider scene only
        pulseInteractionState.onActivate = {
            ledRingAnimator.fadeAll(ColorMode.color(0, 0, 80))
        }

        pulseInteractionState.onUpdate = {
            // check for collisions
            if (updateCollisions()) {
                ledRingAnimator.fadeAll(ColorMode.color(0, 0, 100))
            } else {
                ledRingAnimator.fadeAll(ColorMode.color(0, 0, 80))
            }

            if (!project.leda.enabledInteraction.value) StateResult(offState)
            else if (project.ledaScenePlayer.enabled.value) StateResult(scenePlayerState)
            else if (!poseDetected.currentValue) StateResult(offState)
            else StateResult()
        }

        // random pulse state
        randomPulseState.onUpdate = {
            if (!project.leda.enableRandomPulses.value) StateResult(offState)
            else if (project.ledaScenePlayer.enabled.value) StateResult(scenePlayerState)
            else if (project.leda.enabledInteraction.value && poseDetected.currentValue) StateResult(welcomeState)
            else StateResult()
        }
    }

    override fun setup() {
        // try to find led ring
        val rings = project.spatialLightElements.filterIsInstance<LEDRing>()
        if (rings.isNotEmpty()) {
            ledRing = rings[0]
            ledRingAnimator.light = ledRing
        }

        stateMachine.setup()

        project.leda.colliderSceneOnly.onChanged += {
            if (poses.isNotEmpty()) {
                if (it) {
                    stateMachine.switch(pulseInteractionState)
                } else {
                    stateMachine.switch(poseState)
                }
            }
        }

        project.leda.spotColor.onChanged += { updateSpotLights() }
        project.leda.spotBrightness.onChanged += { updateSpotLights() }
        updateSpotLights()
    }

    override fun update() {
        project.leda.currentState.value = stateMachine.activeStateName

        if (!poseProvider.isRunning.get())
            return

        // receive poses
        poses = poseProvider.poses.take(project.leda.interactorLimit.value)
        poseDetected.update(poses.isNotEmpty())

        // update animation
        ledRingAnimator.update()

        stateMachine.update()
    }

    override fun stop() {
        ledRingAnimator.fadeAll(ColorMode.color(0))
        stateMachine.release()
    }

    override fun dispose() {
    }

    override val isInteracting: Boolean
        get() = poseProvider.poses.isNotEmpty()

    private fun updateCollisions(): Boolean {
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

        return hasCollision
    }

    private fun checkCollision(pose: Pose, collider: LandmarkPulseCollider): Boolean {
        val origin = project.leda.triggerOrigin.value

        var hasCollision = false

        val collisionCandidates = collider.triggeredBy.value.map { landmarkType ->
            val landmarkId = PoseLandmark.values().indexOf(landmarkType)
            val landmark = pose.keypoints[landmarkId]

            val score = landmark.score
            val relativeLandmarkPosition = PVector.sub(origin, pose.keypoints[landmarkId])
            Pair(CollisionCandidate(relativeLandmarkPosition, landmarkType), score)
        }
            .filter { it.second >= project.leda.landmarkMinScore.value }
            .map { it.first }

        if (collider.checkCollision(collisionCandidates)) {
            hasCollision = true
            collider.pulses.forEach {
                project.pulseScene.pulses.add(it.spawn())
            }
        }

        return hasCollision
    }

    private fun updateSpotLights() {
        val lights = project.spatialLightElements.filterIsInstance<LEDSpot>()
        val color = project.leda.spotColor.value.toHSL()
        val brightness = project.leda.spotBrightness.value

        lights.forEachLED {
            it.color.fadeH(color.h.toFloat(), 0.05f)
            it.color.fadeS(color.s.toFloat(), 0.05f)
            it.color.fadeB(color.l.toFloat() * brightness, 0.05f)
        }
    }
}