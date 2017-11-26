package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.control.RelationNumberField
import javafx.scene.control.TextFormatter
import javafx.util.converter.FloatStringConverter
import java.lang.reflect.Field

class FloatProperty(field: Field, obj: Any, val annotation: FloatParameter) : BaseProperty(field, obj) {
    val numberField = RelationNumberField<Float>(TextFormatter(FloatStringConverter()))

    init {
        children.add(numberField)
        numberField.setValue(10.0)

        val model = field.get(obj) as DataModel<Float>
        model.onChanged += {
            numberField.setValue(model.value.toDouble())
        }
        model.fireLatest()

        numberField.setOnAction {
            model.value = numberField.getValue().toFloat()
            propertyChanged(this)
        }
    }
}