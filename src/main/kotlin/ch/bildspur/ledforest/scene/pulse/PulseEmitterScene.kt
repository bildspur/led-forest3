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

class PulseEmitterScene(pulseScene: PulseScene, project: Project, tubes: List<Tube>, name: String = "Pulse Emitter") :
    PulseAnimationScene(name, pulseScene, project, tubes) {

    private val rnd = ExtendedRandom()
    private val easingChoices = listOf(EasingMethod.Linear, EasingMethod.EaseOutQuad, EasingMethod.EaseInQuad)

    private val spawnTimer = ElapsedTimer(fireOnStart = true)

    override fun animatePulses() {
        spawnTimer.duration = (project.pulseEmitter.spawnInterval.value * 1000.0).roundToLong()

        if (!project.pulseEmitter.enableSpawn.value)
            return

        if (spawnTimer.elapsed()) {
            when (project.pulseEmitter.spawnRhythm.value) {
                PulseSpawnRhythm.Regular -> spawnRegular()
                PulseSpawnRhythm.Random -> spawnRandom()
            }
        }
    }

    private fun spawnRegular() {
        val rate = project.pulseEmitter.spawnRate.value
        val interval = (project.pulseEmitter.spawnInterval.value * 1000.0 / rate).roundToInt()

        repeat((0 until rate).count()) {
            val delay = interval * rate
            val pulse = createPulse()
            pulse.delay.value = delay
            project.pulseScene.pulses.add(pulse.spawn())
        }
    }

    private fun spawnRandom() {
        val rate = project.pulseEmitter.spawnRate.value
        repeat((0 until rate).count()) {
            val delay = rnd.randomFloat(0f, project.pulseEmitter.spawnInterval.value * 1000.0f).roundToInt()
            val pulse = createPulse()
            pulse.delay.value = delay
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
        pulse.duration.value = rnd.randomFloat(4000f, 8000f)

        // distance
        pulse.distance.value = 10f

        // width
        pulse.width.value = rnd.randomFloat(3f, 4f)

        // color
        val gs = config.gradientSpectrum.value
        pulse.color.value =
            project.poseInteraction.gradient.color(rnd.randomFloat(gs.low.toFloat(), gs.high.toFloat()))

        // expansion curve
        pulse.expansionCurve.value = easingChoices[rnd.randomInt(max = easingChoices.size - 1)]

        return pulse
    }
}
