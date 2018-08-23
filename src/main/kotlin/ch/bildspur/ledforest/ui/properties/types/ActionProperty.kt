package ch.bildspur.ledforest.ui.properties.types

import ch.bildspur.ledforest.ui.properties.ActionParameter
import javafx.scene.control.Button
import java.lang.reflect.Field

class ActionProperty(field: Field, obj: Any, val annotation: ActionParameter) : BaseProperty(field, obj) {
    val button = Button()

    init {
        button.text = annotation.caption
        val block = field.get(obj) as (() -> Unit)
        button.setOnAction {
            block()
            propertyChanged.invoke(this)
        }
        children.add(button)
    }
}