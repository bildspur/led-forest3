package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.SpatialLightElement
import ch.bildspur.ledforest.ui.control.scene.control.OrbitControls
import ch.bildspur.model.DataModel
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.effect.Bloom
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Box
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape3D
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import tornadofx.add
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread


class TubePreview(val project: DataModel<Project>) : Group() {
    private val refreshRate = 30.0
    private val globalScale = 100.0

    val subScene = SubScene(this, 512.0, 512.0, true, SceneAntialiasing.BALANCED)

    val camera = PerspectiveCamera()
    lateinit var control: OrbitControls

    private val sceneGroup = Group()
    private val ledShapes = ConcurrentHashMap<LED, Shape3D>()

    private var running = true

    init {
        project.onChanged += {
            reset()
        }
        project.fireLatest()

        // setup global scene
        subScene.fill = Color.rgb(15, 15, 15)
        subScene.effect = Bloom(0.1)

        // setup camera
        camera.nearClip = 0.1
        camera.farClip = 10000.0
        subScene.camera = camera

        // add render element
        sceneGroup.transforms.addAll(
                Rotate(-90.0, Rotate.Y_AXIS),
                Rotate(90.0, Rotate.X_AXIS),
                Rotate(-90.0, Rotate.Z_AXIS),
                Scale(globalScale, globalScale, -globalScale)
        )
        children.add(sceneGroup)

        // render thread
        thread(isDaemon = true, start = true) {
            while (running) {
                render()
                Thread.sleep((1000.0 / refreshRate).toLong())
            }
        }

        // setup controls later to be sure node exists
        Platform.runLater {
            control = OrbitControls(camera, subScene)
        }
    }

    fun render() {
        Platform.runLater {
            ledShapes.forEach { (led, shape) ->
                if (shape.material is PhongMaterial) {
                    val mat = (shape.material as PhongMaterial)
                    mat.diffuseColor = led.color.toJavaFXColor()
                }
            }
        }
    }

    fun reset(hook: Boolean = true) {
        Platform.runLater {
            recreateScene()

            project.value.spatialLightElements.forEach { t ->
                updateLEDs(t)
            }
        }

        if (hook)
            hookEvents()
    }

    private fun hookEvents() {
        project.value.spatialLightElements.forEach { tube ->
            tube.position.onChanged += {
                updateLEDs(tube)
            }

            tube.rotation.onChanged += {
                updateLEDs(tube)
            }

            tube.ledCount.onChanged += {
                // todo: fix reset of renderer
                reset(hook = false)
            }
        }
    }

    fun updateLEDs(element: SpatialLightElement) {
        element.leds.forEach {
            val shape = ledShapes[it] ?: return@forEach

            shape.transforms.clear()
            shape.transforms.add(it.position.toTranslate())
        }
    }

    fun recreateScene() {
        sceneGroup.children.clear()
        ledShapes.clear()

        val box = project.value.interaction.interactionBox.value
        val cage = Rectangle(box.x / -2.0, box.y / -2.0, box.x.toDouble(), box.y.toDouble())
        cage.style = "-fx-fill: transparent; -fx-stroke: white; -fx-stroke-width: 0.05;"
        sceneGroup.add(cage)

        project.value.spatialLightElements.forEach { element ->
            element.leds.forEach { led ->
                val width = project.value.visualisation.ledWidth.value.toDouble()
                val ledShape = Box(width * 2, width * 2, element.ledLength.toDouble())

                ledShape.transforms.add(led.position.toTranslate())
                /*
                ledShape.transforms.add(Rotate(90.0 + Math.toDegrees(element.rotation.value.x.toDouble()), Rotate.X_AXIS))
                ledShape.transforms.add(Rotate(Math.toDegrees(element.rotation.value.y.toDouble()), Rotate.Y_AXIS))
                ledShape.transforms.add(Rotate(Math.toDegrees(element.rotation.value.z.toDouble()), Rotate.Z_AXIS))
                 */

                val mat = PhongMaterial(Color.WHITE)
                mat.specularPower = 0.0
                ledShape.material = mat

                sceneGroup.add(ledShape)
                ledShapes[led] = ledShape
            }
        }
    }
}