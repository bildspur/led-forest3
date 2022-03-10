package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.util.toDegrees
import ch.bildspur.ledforest.util.toRadians
import java.lang.reflect.Field

class PVectorAngleProperty(field: Field, obj: Any, val angleAnnotation: PVectorAngleParameter)
    : PVectorProperty(field, obj, null) {

    override fun inMap(value: Float): Double {
        return value.toDegrees().toDouble()
    }

    override fun outMap(value: Double): Float {
        return value.toFloat().toRadians()
    }
}