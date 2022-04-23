package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.ui.control.scene.control.OrbitControls
import ch.bildspur.model.DataModel
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.effect.Bloom
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.*
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import processing.core.PVector
import tornadofx.add
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class InteractionPreview(val project: DataModel<Project>) : Group() {
    private val refreshRate = 30.0
    private val globalScale = 100.0

    val subScene = SubScene(this, 512.0, 512.0, true, SceneAntialiasing.BALANCED)

    val camera = PerspectiveCamera()
    lateinit var control: OrbitControls

    private val sceneGroup = Group()
    private val colliderShapes = ConcurrentHashMap<LandmarkPulseCollider, Shape3D>()
    private lateinit var cameraShape: Shape3D
    private lateinit var rightWristShape: Shape3D

    private var running = true

    init {
        project.onChanged += {
            Platform.runLater {
                recreateScene()
                hookEvents()
            }
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
            Thread.sleep(5000)
            while (running) {
                render()
                Thread.sleep((1000.0 / refreshRate).toLong())
            }
        }

        // setup controls later to be sure node exists
        Platform.runLater {
            Thread.sleep(1000 * 2)
            control = OrbitControls(camera, subScene)
            children.add(control.centerMarker)
        }
    }

    fun render() {
        Platform.runLater {
            val pose = Sketch.instance.pose.poses.firstOrNull()
            if (pose != null) {
                val score = pose.rightWrist.t
                if (score < project.value.leda.landmarkMinScore.value) {
                    rightWristShape.isVisible = false
                    return@runLater
                }

                rightWristShape.transforms.clear()
                val cameraOrigin = project.value.leda.triggerOrigin.value
                rightWristShape.transforms.add(PVector.sub(pose.rightWrist, cameraOrigin).toTranslate())
                rightWristShape.isVisible = true
            } else {
                rightWristShape.isVisible = false
            }
        }
    }

    private fun hookEvents() {
        project.value.leda.landmarkColliders.forEach { collider ->
            collider.location.onChanged += {
                val shape = colliderShapes[collider]!!
                shape.transforms.clear()
                shape.transforms.add(it.toTranslate())
            }
            collider.location.fireLatest()

            collider.radius.onChanged += {
                sceneGroup.children.remove(colliderShapes[collider])
                val shape = Sphere(collider.radius.value.toDouble())
                shape.material = PhongMaterial(Color.LIGHTBLUE)
                colliderShapes[collider] = shape
                sceneGroup.children.add(shape)
            }
        }

        project.value.leda.triggerOrigin.onChanged += {
            cameraShape.transforms.clear()
            val cameraOrigin = project.value.leda.triggerOrigin.value
            cameraShape.transforms.add(PVector.sub(PVector(), cameraOrigin).toTranslate())
        }
        project.value.leda.triggerOrigin.fireLatest()
    }

    fun recreateScene() {
        sceneGroup.children.clear()

        // add colliders
        project.value.leda.landmarkColliders.forEach {
            val shape = Sphere(it.radius.value.toDouble())
            shape.material = PhongMaterial(Color.LIGHTBLUE)
            colliderShapes[it] = shape
            sceneGroup.children.add(shape)
        }

        // add camera
        cameraShape = Box(0.124, 0.026, 0.029)
        cameraShape.material = PhongMaterial(Color.WHITE)
        sceneGroup.children.add(cameraShape)

        rightWristShape = Box(0.1, 0.1, 0.1)
        rightWristShape.isVisible = false
        rightWristShape.material = PhongMaterial(Color.GREEN)
        sceneGroup.children.add(rightWristShape)
    }
}