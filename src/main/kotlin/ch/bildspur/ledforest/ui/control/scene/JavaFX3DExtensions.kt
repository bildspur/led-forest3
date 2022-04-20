package ch.bildspur.ledforest.ui.control.scene

import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate
import processing.core.PVector

fun PVector.toTranslate(): Translate {
    return Translate(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}

fun PVector.toRotation(pivotX: Double = 0.0, pivotY: Double = 0.0, pivotZ: Double = 0.0): List<Rotate> {
    return listOf(
        Rotate(Math.toDegrees(this.x.toDouble()), pivotX, pivotY, pivotZ, Rotate.X_AXIS),
        Rotate(Math.toDegrees(this.y.toDouble()), pivotX, pivotY, pivotZ, Rotate.Y_AXIS),
        Rotate(Math.toDegrees(this.z.toDouble()), pivotX, pivotY, pivotZ, Rotate.Z_AXIS)
    )
}

fun PVector.toRotation(pivot: PVector): List<Rotate> {
    return this.toRotation(pivot.x.toDouble(), pivot.y.toDouble(), pivot.z.toDouble())
}