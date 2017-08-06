package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.ui.control.RelationNumberField
import java.lang.reflect.Field

class NumberProperty(field: Field, obj: Any, val annotation: NumberParameter) : BaseProperty(field, obj) {
    val numberField = RelationNumberField()

    init {
        children.add(numberField)

        numberField.setValue(10.0)

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