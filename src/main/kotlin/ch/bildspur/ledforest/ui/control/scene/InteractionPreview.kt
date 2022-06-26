package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.leda.ColliderState
import ch.bildspur.ledforest.model.leda.LandmarkPulseCollider
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.ui.control.scene.control.OrbitControls
import ch.bildspur.ledforest.ui.control.scene.shapes.Grid
import ch.bildspur.ledforest.ui.control.scene.shapes.WireBox
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
import javafx.scene.shape.Shape3D
import javafx.scene.shape.Sphere
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import processing.core.PVector
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

    private val landmarkShapes = mutableMapOf<PoseLandmark, Shape3D>()

    private var running = true

    private val colliderActiveColor = Color(0.98, 0.4, 0.0, 0.75)
    private val colliderInactiveColor = Color(0.0, 0.31, 0.93, 0.5)

    private val poseColor = Color(0.37, 0.66, 0.09, 0.80)

    private lateinit var iaBox: WireBox

    var rendering = true

    init {
        // add pose shapes
        PoseLandmark.values().forEach {
            val shape = Box(0.1, 0.1, 0.1)
            shape.isVisible = false
            shape.material = PhongMaterial(Color.GREEN)
            landmarkShapes[it] = shape
        }

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
                try {
                    render()
                } catch (ex: Exception) {
                    System.err.println("Error in interaction renderer: ${ex.message}")
                }
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
        if (!Sketch.isInstanceInitialized) {
            return
        }

        if (!rendering) {
            return
        }

        Platform.runLater {
            // update poses
            val pose = Sketch.instance.pose.poses.firstOrNull()

            landmarkShapes.values.forEach { it.isVisible = false }
            val cameraOrigin = project.value.leda.triggerOrigin.value

            if (pose != null) {
                landmarkShapes.keys.forEach {
                    val lm = pose[it]
                    val shape = landmarkShapes[it]!!

                    if (lm.score >= project.value.leda.landmarkMinScore.value) {
                        shape.transforms.clear()
                        shape.transforms.add(PVector.sub(lm, cameraOrigin).toTranslate())
                        shape.isVisible = true
                    }
                }
            }

            // update colliders
            colliderShapes.forEach { (c, s) ->
                s.isVisible = project.value.leda.displayCollider.value

                val mat = (s.material as PhongMaterial)

                if (c.state == ColliderState.Active) {
                    mat.diffuseColor = colliderActiveColor
                } else {
                    mat.diffuseColor = colliderInactiveColor
                }
            }

            // update iabox
            iaBox.transforms.clear()
            iaBox.transforms.add(getInteractionSpaceTranslate())
        }
    }

    private fun hookEvents() {
        project.value.leda.landmarkColliders.forEach { collider ->
            collider.location.onChanged += {
                val shape = colliderShapes[collider]!!
                shape.transforms.clear()
                shape.transforms.add(PVector.sub(PVector(), collider.location.value).toTranslate())
            }
            collider.location.fireLatest()

            collider.radius.onChanged += {
                sceneGroup.children.remove(colliderShapes[collider])
                val shape = Sphere(collider.radius.value.toDouble())
                shape.material = PhongMaterial(colliderInactiveColor)
                colliderShapes[collider] = shape
                shape.transforms.add(PVector.sub(PVector(), collider.location.value).toTranslate())
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

        // add floor
        sceneGroup.children.add(Grid(8.0, 8))

        // add interaction box
        val box = project.value.interaction.interactionSpace.value
        iaBox = WireBox(box.x.toDouble(), box.y.toDouble(), box.z.toDouble())
        iaBox.transforms.add(getInteractionSpaceTranslate())
        sceneGroup.children.add(iaBox)

        // add colliders
        project.value.leda.landmarkColliders.forEach {
            val shape = Sphere(it.radius.value.toDouble())
            shape.material = PhongMaterial(colliderInactiveColor)
            colliderShapes[it] = shape
            sceneGroup.children.add(shape)
        }

        // add camera
        cameraShape = Box(0.124, 0.026, 0.029)
        cameraShape.material = PhongMaterial(Color.WHITE)
        sceneGroup.children.add(cameraShape)

        landmarkShapes.values.forEach {
            sceneGroup.children.add(it)
        }
    }

    fun reset() {
        recreateScene()
        hookEvents()
    }

    private fun getInteractionSpaceTranslate(): Translate {
        val box = project.value.interaction.interactionSpace.value
        val iat = project.value.interaction.interactionSpaceTranslation.value

        return Translate(
            iat.x.toDouble(),
            iat.y.toDouble(),
            iat.z.toDouble() + (box.z / 2.0)
        )
    }
}