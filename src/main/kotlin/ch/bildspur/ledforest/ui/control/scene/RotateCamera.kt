package ch.bildspur.ledforest.ui.control.scene

import javafx.scene.Camera
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.transform.Rotate


class RotateCamera : Group() {
    val camera: Camera
    val xRotate = Rotate(0.0, Rotate.X_AXIS)
    val yRotate = Rotate(0.0, Rotate.Y_AXIS)
    val zRotate = Rotate(0.0, Rotate.Z_AXIS)

    init {
        camera = PerspectiveCamera(true)
        camera.farClip = 6000.0
        camera.nearClip = 0.01
        camera.translateZ = -2000.0
        camera.getTransforms().addAll(xRotate, yRotate, zRotate)
    }
}