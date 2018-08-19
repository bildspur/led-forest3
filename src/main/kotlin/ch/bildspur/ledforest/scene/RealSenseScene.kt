package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.realsense.RealSenseDataProvider
import processing.core.PApplet

class RealSenseScene(project: Project, tubes: List<Tube>, val realSense: RealSenseDataProvider)
    : BaseInteractionScene("RealSense Scene", project, tubes) {

    private val task = TimerTask(0, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction.name }.toList()
    }

    override fun update() {
        if (!realSense.isRunning)
            return

        iaTubes.forEach {
            it.leds.forEachIndexed { i, led -> interactWithLED(i, led, it) }
        }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    override val isInteracting: Boolean
        get() = realSense.activeRegions.isNotEmpty()

    private fun interactWithLED(index: Int, led: LED, tube: Tube) {
        val ledPosition = getLEDPosition(index, tube)

        val nearestRegion = realSense.activeRegions.sortedBy { it.interactionPosition.dist(ledPosition) }.firstOrNull()
                ?: return

        val distance = nearestRegion.interactionPosition.dist(ledPosition)

        // change color / saturation only if it is in reach
        if (distance <= project.realSenseInteraction.interactionDistance.value) {
            led.color.fadeH(200f, 0.1f)
            led.color.fadeS(100f, 0.1f)
        }

        // always change brightness
        led.color.fadeB(PApplet.max(0f,
                PApplet.map(distance, project.realSenseInteraction.interactionDistance.value, 0f, 0f, 100f)),
                0.1f)
    }
}