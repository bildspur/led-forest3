package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.model.DataModel
import javafx.scene.control.TextField
import java.lang.reflect.Field

class StringProperty(field: Field, obj: Any, val annotation: StringParameter) : BaseProperty(field, obj) {

    val textField = TextField()

    init {
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