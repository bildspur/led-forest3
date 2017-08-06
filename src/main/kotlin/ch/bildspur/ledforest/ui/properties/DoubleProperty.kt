package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.ui.control.RelationNumberField
import javafx.scene.control.TextFormatter
import javafx.util.converter.DoubleStringConverter
import java.lang.reflect.Field

class DoubleProperty(field: Field, obj: Any, val annotation: DoubleParameter?) : BaseProperty(field, obj) {
    val numberField = RelationNumberField<Double>(TextFormatter(DoubleStringConverter()))

    init {

        children.add(numberField)
        numberField.setValue(10.0)

        // set value
        when (field.type) {
            Int::class.java -> numberField.setValue((field.get(obj) as Int).toDouble())
            Float::class.java -> numberField.setValue((field.get(obj) as Float).toDouble())
            Double::class.java -> numberField.setValue(field.get(obj) as Double)
        }

        numberField.setOnAction {

            when (field.type) {
                Int::class.java -> field.set(obj, numberField.getValue().toInt())
                Float::class.java -> field.set(obj, numberField.getValue().toFloat())
                Double::class.java -> field.set(obj, numberField.getValue())
            }

            propertyChanged(this)
        }
    }
}