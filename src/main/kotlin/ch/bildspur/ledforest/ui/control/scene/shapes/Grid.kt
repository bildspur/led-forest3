package ch.bildspur.ledforest.ui.control.scene.shapes

import javafx.scene.Group
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle

class Grid(size: Double, fields: Int, strokeWidth: Double = 0.02) : Group() {
    init {
        val hsize = size * 0.5
        val mainLineStyle = "-fx-fill: transparent; -fx-stroke: white; -fx-stroke-width: ${strokeWidth};"
        val subLineStyle = "-fx-fill: transparent; -fx-stroke: gray; -fx-stroke-width: ${strokeWidth * 0.5};"

        for (i in 1 until fields) {
            val h = i * (size / fields) - hsize

            val lineV = Line(h, -hsize, h, hsize)
            lineV.style = subLineStyle
            children.add(lineV)

            val lineH = Line(-hsize, h, hsize, h)
            lineH.style = subLineStyle
            children.add(lineH)
        }

        val cage = Rectangle(-hsize, -hsize, size, size)
        cage.style = mainLineStyle
        children.add(cage)
    }
}