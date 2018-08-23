package ch.bildspur.ledforest.ui.properties.types

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.control.RelationNumberField
import ch.bildspur.ledforest.ui.properties.NumberParameter
import javafx.scene.control.TextFormatter
import javafx.util.converter.NumberStringConverter
import java.lang.reflect.Field

class NumberProperty(field: Field, obj: Any, val annotation: NumberParameter) : BaseProperty(field, obj) {
    val numberField = RelationNumberField<Number>(TextFormatter(NumberStringConverter()))

    init {

        children.add(numberField)
        numberField.setValue(10.0)

        val model = field.get(obj) as DataModel<Number>
        model.onChanged += {
            numberField.setValue(model.value.toDouble())
        }
        model.fireLatest()

        numberField.setOnAction {
            if (model.value is Int)
                model.value = numberField.getValue().toInt()

            if (model.value is Float)
                model.value = numberField.getValue().toFloat()

            if (model.value is Double)
                model.value = numberField.getValue()
            propertyChanged(this)
        }
    }
}