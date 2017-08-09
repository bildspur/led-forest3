package ch.bildspur.ledforest.ui.properties

import javafx.scene.control.ComboBox
import java.lang.reflect.Field


class ArrayProperty(field: Field, obj: Any, val annotation: ArrayParameter) : BaseProperty(field, obj) {

    val box = ComboBox<String>()

    init {
        children.add(box)
    }
}