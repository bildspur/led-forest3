package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.util.stackMatrix
import ch.bildspur.ledforest.util.translate
import processing.core.PApplet
import processing.core.PVector

class RealSenseScene(tubes: List<Tube>) : BaseScene(tubes) {
    // todo: remove ugly grab of data provider
    val sketch = Sketch.instance
    val realSense = sketch.realSense

    var space = Sketch.instance.createGraphics(10, 10, PApplet.P3D)

    private val task = TimerTask(0, { update() })

    override val name: String
        get() = "RealSense Scene"

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

    fun isTrackingAvailable(): Boolean {
        return realSense.activeRegions.isNotEmpty()
    }

    private fun interactWithLED(index: Int, led: LED, tube: Tube) {
        val ledPosition = getLEDPosition(index, tube)
        val nearestRegion = realSense.activeRegions.sortedBy { it.interactionPosition.dist(ledPosition) }.firstOrNull()
                ?: return

        val distance = nearestRegion.interactionPosition.dist(ledPosition)

        // change color / saturation only if it is in reach
        /*
        if (distance <= sketch.project.value.leapInteraction.interactionDistance.value
                || sketch.project.value.leapInteraction.singleColorInteraction.value) {
            led.color.fadeH(PApplet.map(h.rotation.y, -PApplet.PI, PApplet.PI,
                    sketch.project.value.leapInteraction.hueSpectrum.value.lowValue.toFloat(),
                    sketch.project.value.leapInteraction.hueSpectrum.value.highValue.toFloat()), 0.1f)
            led.color.fadeS(PApplet.map(h.grabStrength.value, 1f, 0f, 0f, 100f), 0.1f)
        }

        // always change brightness
        led.color.fadeB(PApplet.max(0f,
                PApplet.map(distance, sketch.project.value.leapInteraction.interactionDistance.value, 0f, 0f, 100f)),
                0.1f)
        */
    }

    private fun getLEDPosition(index: Int, tube: Tube): PVector {
        val position = PVector()
        space.stackMatrix {
            // translate normalizedPosition
            it.translate(tube.position.value)

            // global rotation
            it.rotateX(tube.rotation.value.x)
            it.rotateY(tube.rotation.value.y)
            it.rotateZ(tube.rotation.value.z)

            // translate height
            it.translate(0f, 0f, (if (tube.inverted.value) -1 else 1) * (index * LED.SIZE))

            // rotate shape
            it.rotateX(PApplet.radians(90f))

            position.x = it.modelX(0f, 0f, 0f)
            position.y = it.modelY(0f, 0f, 0f)
            position.z = it.modelZ(0f, 0f, 0f)
        }

        return position
    }
}