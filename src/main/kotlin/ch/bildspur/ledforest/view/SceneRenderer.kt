package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.InteractionHand
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.realsense.RealSenseDataProvider
import ch.bildspur.ledforest.realsense.tracking.ActiveRegion
import ch.bildspur.ledforest.util.*
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PShape

class SceneRenderer(val g: PGraphics,
                    val tubes: List<Tube>,
                    val leap: LeapDataProvider,
                    val realSense: RealSenseDataProvider,
                    val project: Project) : IRenderer {
    private val task = TimerTask(0, { render() }, "SceneRenderer")
    override val timerTask: TimerTask
        get() = task

    lateinit var rodShape: PShape

    override fun setup() {
        project.visualisation.ledWidth.onChanged += {
            setupRod()
        }

        project.visualisation.ledHeight.onChanged += {
            setupRod()
        }

        project.visualisation.ledDetail.onChanged += {
            setupRod()
        }

        project.visualisation.ledDetail.fireLatest()
        project.visualisation.ledWidth.fireLatest()
        project.visualisation.ledHeight.fireLatest()
    }

    override fun render() {
        // apply scale to everything
        g.scale(project.visualisation.globalScaleFactor.value)

        // render tubes
        tubes.forEach { t ->
            g.stackMatrix {
                renderTube(t)
            }
        }

        // render hands
        if (leap.isRunning && project.interaction.isLeapInteractionEnabled.value) {
            leap.hands.forEach {
                renderHand(it)
            }
        }

        // render active regions
        if (realSense.isRunning && project.interaction.isRealSenseInteractionEnabled.value) {
            realSense.activeRegions.forEach { renderActiveRegion(it) }
        }

        // render leapInteraction box
        if (project.interaction.showInteractionInfo.value)
            renderInteractionBox()
    }

    private fun renderTube(tube: Tube) {
        // draw every LED
        for (i in tube.leds.indices) {
            g.pushMatrix()

            // translate normalizedPosition
            g.translate(tube.position.value)

            // global rotation
            g.rotateX(tube.rotation.value.x)
            g.rotateY(tube.rotation.value.y)
            g.rotateZ(tube.rotation.value.z)

            // translate height
            g.translate(0f, 0f, (if (tube.inverted.value) -1 else 1) * (i * project.visualisation.ledHeight.value))

            // rotate shape
            g.rotateX(PApplet.radians(90f))

            g.noStroke()
            g.fill(tube.leds[i].color.color)

            if (project.visualisation.highDetail.value)
                g.shape(rodShape)
            else
                g.box(project.visualisation.ledWidth.value,
                        project.visualisation.ledWidth.value,
                        project.visualisation.ledHeight.value)

            g.popMatrix()
        }
    }

    private fun setupRod() {
        rodShape = g.createRod(project.visualisation.ledWidth.value,
                project.visualisation.ledHeight.value,
                project.visualisation.ledDetail.value.toInt())
        rodShape.disableStyle()
    }

    private fun renderActiveRegion(region: ActiveRegion) {
        // render
        g.pushMatrix()
        g.translate(region.interactionPosition)
        g.fill(255f)
        g.noStroke()
        g.box(10f)
        g.popMatrix()
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

    private fun renderInteractionBox() {
        g.pushMatrix()
        g.noFill()
        g.stroke(255)
        g.box(project.interaction.interactionBox.value.x,
                project.interaction.interactionBox.value.y,
                project.interaction.interactionBox.value.z)
        g.popMatrix()
    }

    override fun dispose() {
    }
}