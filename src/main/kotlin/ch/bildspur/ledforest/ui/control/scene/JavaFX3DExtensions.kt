package ch.bildspur.ledforest.ui.control.scene

import javafx.scene.transform.Rotate
import javafx.scene.transform.Translate
import processing.core.PVector

fun PVector.toTranslate(): Translate {
    return Translate(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}

fun PVector.toRotation(): List<Rotate> {
    return listOf(
        Rotate(Math.toDegrees(this.x.toDouble()), Rotate.X_AXIS),
        Rotate(Math.toDegrees(this.y.toDouble()), Rotate.Y_AXIS),
        Rotate(Math.toDegrees(this.z.toDouble()), Rotate.Z_AXIS)
    )
}