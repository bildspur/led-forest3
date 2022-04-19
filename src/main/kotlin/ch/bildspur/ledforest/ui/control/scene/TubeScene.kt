package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.ui.control.scene.control.ArcBallControl
import ch.bildspur.model.DataModel
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Box
import javafx.scene.shape.Shape3D
import javafx.scene.transform.Translate
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread


class TubeScene(val project: DataModel<Project>) : Group() {
    private val refreshRate = 30.0

    val subScene = SubScene(this, 512.0, 512.0, true, SceneAntialiasing.BALANCED)

    val camera = PerspectiveCamera(true)
    val control = ArcBallControl(camera, subScene)

    private val ledShapes = ConcurrentHashMap<LED, Shape3D>()

    private var running = true

    init {
        project.onChanged += {
            recreateTubes()
        }
        project.fireLatest()

        // setup global scene
        subScene.fill = Color.BLACK
        subScene.camera = camera

        // render thread
        thread(isDaemon = true, start = true) {
            while (running) {
                render()
                Thread.sleep((1000.0 / refreshRate).toLong())
            }
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
        project.value.tubes.forEach { tube ->
            tube.leds.forEach { led ->
                val width = project.value.visualisation.ledWidth.value.toDouble()
                val ledShape = Box(width, tube.ledLength.toDouble(), width)

                val translation = Translate(
                    led.position.x.toDouble(),
                    led.position.y.toDouble(),
                    led.position.z.toDouble()
                )
                ledShape.transforms.add(translation)
                ledShape.material = PhongMaterial(Color.WHITE)

                children.add(ledShape)
                ledShapes[led] = ledShape
            }
        }
    }
}