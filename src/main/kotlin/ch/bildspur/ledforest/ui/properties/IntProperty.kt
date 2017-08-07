package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.control.RelationNumberField
import javafx.scene.control.TextFormatter
import javafx.util.converter.IntegerStringConverter
import java.lang.reflect.Field

class IntProperty(field: Field, obj: Any, val annotation: IntParameter) : BaseProperty(field, obj) {
    val numberField = RelationNumberField<Int>(TextFormatter(IntegerStringConverter()))

    init {

        children.add(numberField)
        numberField.setValue(10.0)

        val model = field.get(obj) as DataModel<Int>
        model.onChanged += {
            numberField.setValue(model.value.toDouble())
        }
        model.fire()

        numberField.setOnAction {
            model.value = numberField.getValue().toInt()
            propertyChanged(this)
        }
    }
}