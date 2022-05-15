package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.ui.control.scene.control.OrbitControls
import ch.bildspur.model.DataModel
import javafx.animation.AnimationTimer
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.effect.Bloom
import javafx.scene.paint.Color
import javafx.scene.shape.Shape3D
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

abstract class Base3DScene<E>(
    val project: DataModel<Project>,
    background: Color = Color.rgb(15, 15, 15),
    bloom: Double = 0.1,
    private val refreshRate: Double = 30.0,
    globalScale: Double = 100.0
) : Group() {

    val subScene = SubScene(this, 512.0, 512.0, true, SceneAntialiasing.BALANCED)

    val camera = PerspectiveCamera()
    lateinit var control: OrbitControls

    private val sceneGroup = Group()
    val shapes = ConcurrentHashMap<E, Shape3D>()

    private var running = true
    var rendering = true

    init {
        project.onChanged += {
            resetScene()
        }
        project.fireLatest()

        // setup global scene
        subScene.fill = background

        if (bloom > 0) {
            subScene.effect = Bloom(bloom)
        }

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
                if (rendering) {
                    renderLoop()
                }
                Thread.sleep((1000.0 / refreshRate).toLong())
            }
        }

        // setup controls later to be sure node exists
        Platform.runLater {
            control = OrbitControls(camera, subScene)
        }
    }

    private fun renderLoop() {
        Platform.runLater {
            shapes.forEach { (element, shape) ->
                updateShape(element, shape)
            }
        }
    }

    fun resetScene() {
        shapes.clear()

        Platform.runLater {
            sceneGroup.children.clear()
            recreateScene(sceneGroup)
        }
    }

    internal abstract fun recreateScene(root: Group)
    internal abstract fun updateShape(element: E, shape: Shape3D)
}