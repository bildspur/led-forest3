package ch.bildspur.ledforest.ui.control.scene.control

import ch.bildspur.math.Float2
import javafx.scene.Camera
import javafx.scene.Node
import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate
import kotlin.math.max
import kotlin.math.min

class ArcBallControl(
    val camera: Camera, val node: Node,
    var zoomSpeed: Double = 5.0, var minZoom: Double = -50.0, var maxZoom: Double = -500.0
) {
    val rotateX = Rotate(0.0, Rotate.X_AXIS)
    val rotateY = Rotate(0.0, Rotate.Y_AXIS)
    val rotateZ = Rotate(0.0, Rotate.Z_AXIS)

    val translate = Translate(0.0, 0.0, -100.0)


    private var lastPosition = Float2()
    private var dragging = false

    init {
        camera.nearClip = 0.1
        camera.farClip = 10000.0

        camera.transforms.addAll(rotateX, rotateY, rotateZ, translate)

        // zoom
        node.setOnZoom {
            if (it.zoomFactor > 1) {
                translate.z += zoomSpeed
            } else {
                translate.z -= zoomSpeed
            }

            translate.z = min(max(translate.z, maxZoom), minZoom)
        }

        node.setOnMousePressed {
            lastPosition = Float2(it.x.toFloat(), it.y.toFloat())
            dragging = true
        }

        // rotation
        node.setOnMouseDragged {
            if (!dragging) return@setOnMouseDragged

            val current = Float2(it.x.toFloat(), it.y.toFloat())
            val delta = lastPosition - current

            if (it.isShiftDown) {
                translate.x += delta.x.toDouble()
                translate.y += delta.y.toDouble()
            } else {
                rotateX.angle += delta.y.toDouble()
                rotateY.angle -= delta.x.toDouble()
            }

            lastPosition = current
        }

        node.setOnMouseReleased {
            dragging = false
        }
    }
}