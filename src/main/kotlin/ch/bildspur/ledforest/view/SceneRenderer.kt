package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.leap.InteractionHand
import ch.bildspur.ledforest.leap.LeapDataProvider
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.pose.Pose
import ch.bildspur.ledforest.pose.PoseDataProvider
import ch.bildspur.ledforest.realsense.RealSenseDataProvider
import ch.bildspur.ledforest.realsense.tracking.ActiveRegion
import ch.bildspur.ledforest.scene.mapPose
import ch.bildspur.ledforest.util.*
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PShape

class SceneRenderer(val g: PGraphics,
                    val tubes: List<Tube>,
                    val leap: LeapDataProvider,
                    val realSense: RealSenseDataProvider,
                    val poseProvider: PoseDataProvider,
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
        if(project.visualisation.disableViewRendering.value) {
            return
        }

        // apply scale to everything
        g.pushMatrix()
        g.scale(project.visualisation.globalScaleFactor.value)

        // render floor
        if (project.visualisation.displayFloor.value)
            renderFloor()

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

        // render poses
        if (poseProvider.isRunning.get() && project.interaction.isPoseInteractionEnabled.value) {
            poseProvider.poses.forEach { renderPose(it) }
        }

        // render leapInteraction box
        if (project.visualisation.displayDebugInformation.value)
            renderInteractionBox()

        g.popMatrix()
    }

    private fun renderFloor() {
        g.pushMatrix()
        g.fill(28f)
        g.noStroke()
        g.translate(0f, 0f, project.visualisation.floorZHeight.value / -2f)
        g.box(project.interaction.interactionBox.value.x,
                project.interaction.interactionBox.value.y,
                project.visualisation.floorZHeight.value)
        g.popMatrix()
    }

    private fun renderTube(tube: Tube) {
        // draw every LED
        for (i in tube.leds.indices) {
            // todo: only do translation once and not for every LED!
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

        // render rect around selected tube
        if(!project.visualisation.displayDebugInformation.value) return

        g.pushMatrix()

        // translate normalizedPosition
        g.translate(tube.position.value)

        // global rotation
        g.rotateX(tube.rotation.value.x)
        g.rotateY(tube.rotation.value.y)
        g.rotateZ(tube.rotation.value.z)

        // render origin indicator
        g.noFill()
        g.stroke(ColorMode.color(280f, 80f, 100f))
        g.pushMatrix()
        val originBoxWidth = project.visualisation.ledWidth.value * 3f
        g.translate(0f, 0f, originBoxWidth * 0.5f)
        g.box(originBoxWidth)
        g.popMatrix()

        // render rect around selected tube
        if (project.visualisation.displayCubeCage.value || tube.isSelected.value) {
            g.noFill()
            val hue = if(tube.isSelected.value) 160f else 30f
            val brightness = if(tube.isSelected.value) 100f else 60f
            g.stroke(ColorMode.color(hue, 80f, brightness))

            val cageWidth = project.visualisation.ledWidth.value * 2f
            val cageHeight = (project.visualisation.ledHeight.value * tube.ledCount.value) + cageWidth

            g.translate(0f, 0f, cageHeight * 0.5f)
            g.box(cageWidth, cageWidth, cageHeight)
        }

        g.popMatrix()
    }

    private fun setupRod() {
        rodShape = g.createRod(project.visualisation.ledWidth.value,
                project.visualisation.ledHeight.value,
                project.visualisation.ledDetail.value)
        rodShape.disableStyle()
    }

    private fun renderActiveRegion(region: ActiveRegion) {
        // render
        g.pushMatrix()
        g.translate(region.interactionPosition)
        g.fill(255f)
        g.noStroke()
        g.box(0.5f)
        g.popMatrix()
    }

    private fun renderHand(hand: InteractionHand) {
        g.pushMatrix()
        g.translate(hand.position)
        g.rotate(hand.rotation)
        g.noFill()
        g.stroke(ColorMode.color(255))
        g.strokeWeight(0.05f)
        g.sphereDetail(PApplet.map(hand.grabStrength.value, 0f, 1f, 5f, 20f).toInt())
        g.sphere(0.5f)
        g.popMatrix()
    }

    private fun renderPose(pose: Pose) {
        val position = pose.easedPosition.mapPose()
        g.pushMatrix()
        g.translate(position.x, position.y, position.z)
        g.noFill()
        g.sphereDetail(8)
        g.stroke(ColorMode.color(360.0f * (pose.id % 10) / 10.0f, 80f, 100f))
        g.sphere(0.2f)
        g.popMatrix()
    }

    private fun renderInteractionBox() {
        g.pushMatrix()
        g.strokeWeight(0.05f)
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