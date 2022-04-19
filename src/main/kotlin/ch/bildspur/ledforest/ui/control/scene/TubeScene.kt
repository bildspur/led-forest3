package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.ui.control.scene.control.OrbitControls
import ch.bildspur.model.DataModel
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Box
import javafx.scene.shape.Shape3D
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread


class TubeScene(val project: DataModel<Project>) : Group() {
    private val refreshRate = 30.0
    private val globalScale = 100.0

    val subScene = SubScene(this, 512.0, 512.0, true, SceneAntialiasing.BALANCED)

    val camera = PerspectiveCamera()
    lateinit var control: OrbitControls

    private val ledGroup = Group()
    private val ledShapes = ConcurrentHashMap<LED, Shape3D>()

    private var running = true

    init {
        project.onChanged += {
            recreateTubes()
        }
        project.fireLatest()

        // setup global scene
        subScene.fill = Color.DARKGRAY
        camera.nearClip = 0.1
        camera.farClip = 10000.0
        subScene.camera = camera

        // add render element
        ledGroup.transforms.addAll(
            Rotate(90.0, Rotate.X_AXIS),
            Rotate(-90.0, Rotate.Z_AXIS),
            Scale(globalScale, globalScale, globalScale)
        )
        children.add(ledGroup)

        // render thread
        thread(isDaemon = true, start = true) {
            while (running) {
                render()
                Thread.sleep((1000.0 / refreshRate).toLong())
            }
        }

        // setup controls later to be sure node exists
        Platform.runLater {
            Thread.sleep(1000 * 2)
            control = OrbitControls(camera, subScene)
            // children.add(control.centerMarker)
        }
    }

    fun render() {
        ledShapes.forEach { (led, shape) ->
            if (shape.material is PhongMaterial) {
                (shape.material as PhongMaterial).diffuseColor = led.color.toJavaFXColor()
            }
        }
    }

    fun recreateTubes() {
        ledGroup.children.clear()

        project.value.tubes.forEach { tube ->
            tube.leds.forEach { led ->
                val width = project.value.visualisation.ledWidth.value.toDouble()
                val ledShape = Box(width, tube.ledLength.toDouble(), width)

                // ledShape.transforms.addAll(tube.rotation.value.toRotation())
                ledShape.transforms.add(led.position.toTranslate())

                ledShape.material = PhongMaterial(Color.WHITE)

                ledGroup.children.add(ledShape)
                ledShapes[led] = ledShape
            }
        }
    }
}