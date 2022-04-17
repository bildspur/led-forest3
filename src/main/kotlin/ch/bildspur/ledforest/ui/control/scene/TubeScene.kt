package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.ui.control.scene.control.ArcBallControl
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.paint.Color
import javafx.scene.shape.Box
import javafx.scene.shape.CullFace


class TubeScene : Group() {
    val subScene = SubScene(this, 512.0, 512.0, true, SceneAntialiasing.BALANCED)

    val camera = PerspectiveCamera(true)
    val control = ArcBallControl(camera, subScene)

    init {
        subScene.fill = Color.LIGHTCORAL

        val box = Box(10.0, 10.0, 10.0)
        box.cullFace = CullFace.NONE
        this.children.add(box)

        subScene.camera = camera
    }
}