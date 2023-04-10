package ch.bildspur.ledforest.scene.pulse

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.util.ExtendedRandom

class CenterPulseScene (pulseScene: PulseScene, project: Project, tubes: List<Tube>) :
    PulseAnimationScene("Center Pulse", pulseScene, project, tubes) {

    private val rnd = ExtendedRandom()
    private val easingChoices = listOf(EasingMethod.Linear, EasingMethod.EaseOutQuad, EasingMethod.EaseInQuad)
    override fun animatePulses() {
        val pulse = Pulse()
        pulse.location.value.x = 0f
        pulse.location.value.y = 0f

        pulse.duration.value = rnd.randomFloat(4000f, 8000f)
        pulse.distance.value = 10f

        val gs = project.leda.gradientSpectrum.value
        pulse.color.value =
            project.poseInteraction.gradient.color(rnd.randomFloat(gs.low.toFloat(), gs.high.toFloat()))
        pulse.width.value = rnd.randomFloat(3f, 4f)
        pulse.expansionCurve.value = easingChoices[rnd.randomInt(max = easingChoices.size - 1)]

        project.pulseScene.pulses.add(pulse.spawn())
    }
}