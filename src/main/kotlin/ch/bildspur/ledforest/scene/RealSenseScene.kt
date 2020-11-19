package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.realsense.RealSenseDataProvider
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.ledforest.util.limit
import processing.core.PApplet

class RealSenseScene(project: Project, tubes: List<Tube>, val realSense: RealSenseDataProvider)
    : BaseInteractionScene("RealSense Scene", project, tubes) {

    private val task = TimerTask(0, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()
    var cubeTubes = emptyList<Tube>()

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction }.toList()
        cubeTubes = tubes.filter { it.tag.value == TubeTag.CubeBottom || it.tag.value == TubeTag.CubeTop }.toList()

        // turn off cube leds
        cubeTubes.forEach {
            it.leds.forEach {
                it.color.fadeH(0f, 0.1f)
                it.color.fadeS(0f, 0.05f)
                it.color.fadeB(0f, 0.05f)
            }
        }
    }

    override fun update() {
        if (!realSense.isRunning)
            return

        // interaction tubes
        iaTubes.forEach {
            it.leds.forEachIndexed { i, led -> interactWithLED(i, led, it) }
        }

        // cube tubes (pulsing)
        cubeTubes.first().leds.first().let {
            if (!it.color.isFading) {
                val nextBrightness = if (it.color.current.z < 16f) 30f else 15f
                cubeTubes.forEachLED {
                    it.color.fadeH(300f, 0.1f)
                    it.color.fadeS(100f, 0.05f)
                    it.color.fadeB(nextBrightness, project.realSenseInteraction.pulseSpeed.value)
                }
            }
        }
    }

    override fun stop() {

    }

    override fun dispose() {

    }

    override val isInteracting: Boolean
        get() = realSense.activeRegions.isNotEmpty()

    private fun interactWithLED(index: Int, led: LED, tube: Tube) {
        val rsi = project.realSenseInteraction
        val ledPosition = getLEDPosition(index, tube)

        val nearestRegion = realSense.activeRegions.sortedBy { it.interactionPosition.dist(ledPosition) }.firstOrNull()
                ?: return

        val distance = nearestRegion.interactionPosition.dist(ledPosition)

        // change color / saturation only if it is in reach
        if (distance <= project.realSenseInteraction.interactionDistance.value) {
            if (rsi.mapDepthToColor.value) {
                led.color.fadeH(PApplet.map(nearestRegion.normalizedPosition.z.limit(0f, 1f),
                        0f, 1f,
                        rsi.hueSpectrum.value.low.toFloat(), rsi.hueSpectrum.value.high.toFloat()), 0.1f)
            } else {
                led.color.fadeH(200f, 0.1f)
            }

            led.color.fadeS(100f, 0.1f)
        }

        // always change brightness
        led.color.fadeB(PApplet.max(0f,
                PApplet.map(distance, rsi.interactionDistance.value, 0f, 0f, 100f)),
                0.1f)
    }
}