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

class LeapMotionScene(tubes: List<Tube>) : BaseScene(tubes) {
    // todo: remove ugly grab of data provider
    val sketch = (Sketch.instance as Sketch)
    val leap = sketch.leapMotion

    var space = Sketch.instance.createGraphics(10, 10, PApplet.P3D)

    private val task = TimerTask(0, { update() })

    override val name: String
        get() = "LeapMotion Scene"

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

    fun isLeapAvailable(): Boolean {
        return leap.isRunning && leap.hands.isNotEmpty()
    }

    private fun interactWithLED(index: Int, led: LED, tube: Tube) {
        val ledPosition = getLEDPosition(index, tube)

        if (leap.hands.isEmpty())
            return

        try {
            val h = leap.hands.sortedBy { it.position.dist(ledPosition) }.firstOrNull() ?: return

            val distance = h.position.dist(ledPosition)

            // change color / saturation only if it is in reach
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
        } catch (ex: Exception) {
            println("LCB 0: ${ex.message}")
            return
        }
    }

    private fun getLEDPosition(index: Int, tube: Tube): PVector {
        val position = PVector()
        space.stackMatrix {
            // translate position
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