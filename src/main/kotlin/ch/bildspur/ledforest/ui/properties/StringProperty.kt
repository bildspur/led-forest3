package ch.bildspur.ledforest.ui.properties

import javafx.scene.control.TextField
import java.lang.reflect.Field

class StringProperty(field: Field, obj: Any, val annotation: StringParameter) : BaseProperty(field, obj) {

    val textField = TextField()

    init {
        children.add(textField)
        textField.text = field.get(obj) as String

        textField.setOnAction {
            field.set(obj, textField.text)
            propertyChanged(this)
        }
    }
}