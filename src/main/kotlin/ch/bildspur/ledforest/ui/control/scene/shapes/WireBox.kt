package ch.bildspur.ledforest.ui.control.scene.shapes

import javafx.scene.Group
import javafx.scene.shape.Box
import javafx.scene.shape.DrawMode


class WireBox(xs: Double, ys: Double, zs: Double, strokeWidth: Double = 0.02) : Group() {
    init {
        val mainLineStyle = "-fx-fill: transparent; -fx-stroke: white; -fx-stroke-width: ${strokeWidth};"

        val box = Box(xs, ys, zs)
        box.drawMode = DrawMode.LINE
        box.style = mainLineStyle
        children.add(box)
    }
}