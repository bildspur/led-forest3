package ch.bildspur.ledforest.ui.properties.types

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.StringParameter
import javafx.scene.control.TextField
import java.lang.reflect.Field

class StringProperty(field: Field, obj: Any, val annotation: StringParameter) : BaseProperty(field, obj) {

    val textField = TextField()

    init {
        textField.isEditable = annotation.isEditable
        if (!annotation.isEditable) {
            // set to read only
            textField.style = "-fx-background-color: rgba(200, 200, 200, 0.3);\n" +
                    "-fx-border-color: rgba(200, 200, 200, 1.0);\n" +
                    "-fx-border-width: 1px;"
        }

        children.add(textField)

        // setup binding
        val model = field.get(obj) as DataModel<String>
        model.onChanged += {
            textField.text = model.value
        }
        model.fireLatest()

        textField.setOnAction {
            model.value = textField.text
            propertyChanged(this)
        }
    }
}