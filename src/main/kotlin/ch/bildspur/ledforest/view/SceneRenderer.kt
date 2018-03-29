package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.InteractionHand
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.*
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PShape

class SceneRenderer(val g: PGraphics, val tubes: List<Tube>, val leap: LeapDataProvider, val project: Project) : IRenderer {
    private val task = TimerTask(0, { render() }, "SceneRenderer")
    override val timerTask: TimerTask
        get() = task

    lateinit var rodShape: PShape


    override fun setup() {
        project.tubeDetail.onChanged += {
            setupRod()
        }
        project.tubeDetail.fireLatest()
    }

    override fun render() {

        // render tubes
        tubes.forEach { t ->
            g.stackMatrix {
                renderTube(t)
            }
        }

        // render hands
        if (leap.isRunning) {
            try {
                leap.hands.forEach {
                    renderHand(it)
                }
            } catch (ex: Exception) {
                println("LCB 1: ${ex.message}")
            }
        }

        // render interaction box
        if (project.interaction.showInteractionInfo.value)
            renderInteractionInfo()
    }

    private fun renderTube(tube: Tube) {
        // draw every LED
        for (i in tube.leds.indices) {
            g.pushMatrix()

            // translate position
            g.translate(tube.position.value)

            // global rotation
            g.rotateX(tube.rotation.value.x)
            g.rotateY(tube.rotation.value.y)
            g.rotateZ(tube.rotation.value.z)

            // translate height
            g.translate(0f, 0f, (if (tube.inverted.value) -1 else 1) * (i * LED.SIZE))

            // rotate shape
            g.rotateX(PApplet.radians(90f))

            g.noStroke()
            g.fill(tube.leds[i].color.color)

            if (project.highDetail.value)
                g.shape(rodShape)
            else
                g.box(LED.SIZE)

            g.popMatrix()
        }
    }

    private fun setupRod() {
        rodShape = g.createRod(Tube.WIDTH, LED.SIZE, project.tubeDetail.value.toInt())
        rodShape.disableStyle()
    }

    private fun renderHand(hand: InteractionHand) {
        g.pushMatrix()
        g.translate(hand.position)
        g.rotate(hand.rotation)
        g.noFill()
        g.stroke(ColorMode.color(255))
        g.strokeWeight(2f)
        g.sphereDetail(PApplet.map(hand.grabStrength.value, 0f, 1f, 5f, 20f).toInt())
        g.sphere(20f)
        g.popMatrix()
    }

    private fun renderInteractionInfo() {
        g.pushMatrix()
        g.noFill()
        g.stroke(255)
        g.box(leap.interactionBox.x, leap.interactionBox.y, leap.interactionBox.z)
        g.popMatrix()
    }

    override fun dispose() {
    }
}