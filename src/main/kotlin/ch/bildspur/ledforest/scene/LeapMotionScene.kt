package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import processing.core.PApplet

class LeapMotionScene(project: Project, tubes: List<Tube>, val leap: LeapDataProvider)
    : BaseInteractionScene("LeapMotion Scene", project, tubes) {
    private val task = TimerTask(0, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction.name }.toList()
    }

    override fun update() {
        if (leap.isRunning)
            iaTubes.forEach {
                it.leds.forEachIndexed { i, led -> interactWithLED(i, led, it) }
            }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    override val isInteracting: Boolean
        get() = leap.isRunning && leap.hands.isNotEmpty()

    private fun interactWithLED(index: Int, led: LED, tube: Tube) {
        val ledPosition = getLEDPosition(index, tube)
        val hands = leap.hands

        if (hands.isEmpty())
            return

        val h = hands.sortedBy { it.position.dist(ledPosition) }.firstOrNull() ?: return

        val distance = h.position.dist(ledPosition)

        // change color / saturation only if it is in reach
        if (distance <= project.leapInteraction.interactionDistance.value
                || project.leapInteraction.singleColorInteraction.value) {
            led.color.fadeH(PApplet.map(h.rotation.y, -PApplet.PI, PApplet.PI,
                    project.leapInteraction.hueSpectrum.value.lowValue.toFloat(),
                    project.leapInteraction.hueSpectrum.value.highValue.toFloat()), 0.1f)
            led.color.fadeS(PApplet.map(h.grabStrength.value, 1f, 0f, 0f, 100f), 0.1f)
        }

        // always change brightness
        led.color.fadeB(PApplet.max(0f,
                PApplet.map(distance, project.leapInteraction.interactionDistance.value, 0f, 0f, 100f)),
                0.1f)
    }
}