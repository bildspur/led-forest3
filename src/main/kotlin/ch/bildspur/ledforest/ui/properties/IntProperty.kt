package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.ui.control.RelationNumberField
import javafx.scene.control.TextFormatter
import javafx.util.converter.IntegerStringConverter
import java.lang.reflect.Field

class IntProperty(field: Field, obj: Any, val annotation: IntParameter) : BaseProperty(field, obj) {
    val numberField = RelationNumberField<Int>(TextFormatter(IntegerStringConverter()))

    init {

        children.add(numberField)
        numberField.setValue(10.0)

        // set value
        numberField.setValue((field.get(obj) as Int).toDouble())

        numberField.setOnAction {
            field.set(obj, numberField.getValue().toInt())
            propertyChanged(this)
        }
    }
}