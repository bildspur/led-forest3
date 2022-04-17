package ch.bildspur.ledforest.ui.control.scene

import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.input.ScrollEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Box
import javafx.scene.shape.CullFace


class TubeScene : Group() {
    val scene = SubScene(this, 512.0, 512.0, true, SceneAntialiasing.BALANCED)

    init {
        scene.fill = Color.BLACK

        val box = Box(100.0, 100.0, 100.0)
        box.cullFace = CullFace.NONE
        this.children.add(box)

        val camera = RotateCamera()

        scene.onScroll = EventHandler { e: ScrollEvent ->
            println("scrolling")
            camera.xRotate.angle = camera.xRotate.angle + e.deltaY / 10
            camera.yRotate.angle = camera.yRotate.angle - e.deltaX / 10
            camera.translateX = camera.translateX + e.deltaX
            camera.translateY = camera.translateY + e.deltaY
        }

        scene.camera = camera.camera
    }
}