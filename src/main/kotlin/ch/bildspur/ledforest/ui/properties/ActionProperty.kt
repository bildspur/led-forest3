package ch.bildspur.ledforest.ui.properties

import javafx.scene.control.Button
import java.lang.reflect.Field

class ActionProperty(field: Field, obj: Any, val annotation: ActionParameter) : BaseProperty(field, obj) {
    val button = Button()

    init {
        button.text = annotation.caption
        val block = field.get(obj) as (() -> Unit)
        button.setOnAction { block() }
        children.add(button)
    }
}