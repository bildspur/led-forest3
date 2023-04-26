package ch.bildspur.ledforest.scene.pulse

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.model.pulse.PulseSpawnRhythm
import ch.bildspur.ledforest.util.ExtendedRandom
import ch.bildspur.ledforest.util.toPVector
import ch.bildspur.timer.ElapsedTimer
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class PulseEmitterScene(pulseScene: PulseScene, project: Project, tubes: List<Tube>, name: String = "PulseEmitter") :
    PulseAnimationScene(name, pulseScene, project, tubes) {

    private val rnd = ExtendedRandom()
    private val easingChoices = listOf(EasingMethod.Linear, EasingMethod.EaseOutQuad, EasingMethod.EaseInQuad)

    private val spawnTimer = ElapsedTimer(fireOnStart = true)

    init {
        // bind bidirectional settings
        project.pulseEmitter.offColor.bindBidirectional(project.pulseScene.offColor)
        project.pulseScene.offColor.fireLatest()
        project.pulseEmitter.offThreshold.bindBidirectional(project.pulseScene.offThreshold)
        project.pulseScene.offThreshold.fireLatest()
    }

    override fun animatePulses() {
        spawnTimer.duration = (project.pulseEmitter.spawnInterval.value * 1000.0).roundToLong()

        if (!project.pulseEmitter.enableSpawn.value)
            return

        if (spawnTimer.elapsed()) {
            when (project.pulseEmitter.spawnRhythm.value) {
                PulseSpawnRhythm.Regular -> spawnRegular()
                PulseSpawnRhythm.Random -> spawnRandom()
                PulseSpawnRhythm.Burst -> spawnBurst()
            }
        }
    }

    private fun spawnRegular() {
        val rate = project.pulseEmitter.spawnRate.value
        val interval = (project.pulseEmitter.spawnInterval.value * 1000.0 / rate).roundToInt()

        repeat((0 until rate).count()) {
            val delay = interval * it
            val pulse = createPulse()
            pulse.delay.value += delay
            project.pulseScene.pulses.add(pulse.spawn())
        }
    }

    private fun spawnRandom() {
        val rate = project.pulseEmitter.spawnRate.value
        repeat((0 until rate).count()) {
            val delay = rnd.randomFloat(0f, project.pulseEmitter.spawnInterval.value * 1000.0f).roundToInt()
            val pulse = createPulse()
            pulse.delay.value += delay
            project.pulseScene.pulses.add(pulse.spawn())
        }
    }

    private fun spawnBurst() {
        val rate = project.pulseEmitter.spawnRate.value
        repeat((0 until rate).count()) {
            val pulse = createPulse()
            project.pulseScene.pulses.add(pulse.spawn())
        }
    }

    private fun createPulse(): Pulse {
        val config = project.pulseEmitter
        val pulse = Pulse()

        // location
        pulse.location.value = config.spawnLocation.value.toPVector()

        if (config.randomizeLocationX.value)
            pulse.location.value.x = rnd.randomFloat(config.locationRangeX.value)

        if (config.randomizeLocationY.value)
            pulse.location.value.y = rnd.randomFloat(config.locationRangeY.value)

        if (config.randomizeLocationZ.value)
            pulse.location.value.z = rnd.randomFloat(config.locationRangeZ.value)

        // duration
        pulse.duration.value = config.pulseDuration.value * 1000f
        if (config.randomizePulseDuration.value)
            pulse.duration.value = rnd.randomFloat(config.pulseDurationRange.value) * 1000f

        // delay
        pulse.delay.value = (config.pulseDelay.value * 1000f).roundToInt()
        if (config.randomizePulseDelay.value)
            pulse.delay.value = (rnd.randomFloat(config.pulseDelayRange.value) * 1000f).roundToInt()

        // distance
        pulse.distance.value = config.pulseDistance.value
        if (config.randomizePulseDistance.value)
            pulse.distance.value = rnd.randomFloat(config.pulseDistanceRange.value)

        // width
        pulse.width.value = config.pulseWidth.value
        if (config.randomizePulseWidth.value)
            pulse.width.value = rnd.randomFloat(config.pulseWidthRange.value)

        // color
        val gs = config.gradientSpectrum.value
        pulse.color.value =
            project.poseInteraction.gradient.color(rnd.randomFloat(gs.low.toFloat(), gs.high.toFloat()))

        // expansion curve
        pulse.expansionCurve.value = config.expansionCurve.value
        if (config.randomizeExpansionCurve.value)
            pulse.expansionCurve.value = easingChoices[rnd.randomInt(max = easingChoices.size - 1)]

        return pulse
    }
}
