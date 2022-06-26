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
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.Pose
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.statemachine.*
import ch.bildspur.ledforest.util.ColorMode
import ch.bildspur.ledforest.util.Debouncer
import ch.bildspur.ledforest.util.ExtendedRandom
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
    private val ledRingAnimator = LightAnimator()

    private var poseDetected = Debouncer(500, false)
    private var poses = emptyList<Pose>()

    // states
    val idleState = SceneState(idleScene)
    val poseState = SceneState(poseScene)
    val pulseState = SceneState(pulseScene)
    val pulseInteractionState = SceneState(pulseScene)
    val randomPulseState = CustomState("RandomPulse")

    val offState = TimedState("Off", 250L, idleState)
    val welcomeState = TimedState("Welcome", 2000L, poseState)

    val stateMachine = StateMachine(offState)

    val rnd = ExtendedRandom()
    val easingChoices = listOf(EasingMethod.Linear, EasingMethod.EaseOutQuad, EasingMethod.EaseInQuad)

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
            ledRingAnimator.fadeAll(ColorMode.color(200))
            if (project.leda.enabledInteraction.value && poseDetected.currentValue) StateResult(welcomeState)
            else if (project.leda.enableRandomPulses.value) StateResult(randomPulseState)
            else StateResult()
        }

        welcomeState.onActivate = {
            ledRingAnimator.fadeAll(ColorMode.color(50))

            // check which scene should follow
            welcomeState.nextState = if (project.leda.colliderSceneOnly.value) {
                pulseInteractionState
            } else {
                poseState
            }

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

        // direct interaction scene
        poseState.onActivate = {
            ledRingAnimator.fadeAll(ColorMode.color(100))
        }

        poseState.onUpdate = {
            // check for collisions
            val hasCollision = updateCollisions()

            if (!project.leda.enabledInteraction.value) StateResult(offState)
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
            ledRingAnimator.fadeAll(ColorMode.color(100))
        }

        pulseInteractionState.onUpdate = {
            // check for collisions
            updateCollisions()

            if (!project.leda.enabledInteraction.value) StateResult(offState)
            else if (!poseDetected.currentValue) StateResult(offState)
            else StateResult()
        }

        // random pulse state
        randomPulseState.onActivate = {
            pulseScene.setup()
        }
        randomPulseState.onUpdate = {
            if (rnd.randomBoolean(project.leda.pulseRandomFactor.value)) {
                val pulse = Pulse()
                pulse.location.value.x = rnd.randomFloat(-4f, 4f)
                // pulse.location.value.y = rnd.randomFloat(-4f, 4f)

                pulse.duration.value = rnd.randomFloat(4000f, 8000f)
                pulse.distance.value = 10f

                val gs = project.leda.gradientSpectrum.value
                pulse.color.value =
                    project.poseInteraction.gradient.color(rnd.randomFloat(gs.low.toFloat(), gs.high.toFloat()))
                pulse.width.value = rnd.randomFloat(3f, 4f)
                pulse.expansionCurve.value = easingChoices[rnd.randomInt(max = easingChoices.size - 1)]

                project.pulseScene.pulses.add(pulse.spawn())
            }

            pulseScene.update()

            if (!project.leda.enableRandomPulses.value) StateResult(offState)
            else if (project.leda.enabledInteraction.value && poseDetected.currentValue) StateResult(welcomeState)
            else StateResult()
        }
        randomPulseState.onDeactivate = {
            pulseScene.stop()
        }
    }

    override fun setup() {
        // try to find led ring
        ledRing = project.spatialLightElements.first { it is LEDRing } as LEDRing
        ledRingAnimator.light = ledRing

        stateMachine.setup()
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
}