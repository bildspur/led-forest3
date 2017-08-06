package ch.bildspur.ledforest.ui.properties

import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import processing.core.PVector
import tornadofx.*
import java.lang.reflect.Field

class PVectorProperty(field: Field, obj: Any, val annotation: PVectorParameter) : BaseProperty(field, obj) {
    init {
        val box = VBox()
        box.spacing = 5.0

        val vector = field.get(obj) as PVector
        val params = readParameters(vector)

        params.forEach {
            // warning this really skips the closure!
            if (it.name == "array")
                return@forEach

            val valueProperty = DoubleProperty(it, vector, null)
            val label = Label("${it.name}:")

            valueProperty.numberField.prefWidth = 200.0 - 20.0
            valueProperty.numberField.isShowRange = false
            label.prefWidth = 20.0

            valueProperty.propertyChanged += {
                propertyChanged(it)
            }

            box.add(HBox(label, valueProperty))
        }

        children.add(box)
    }

    private fun readParameters(obj: Any): Array<Field> {
        val c = obj.javaClass

        val fields = c.declaredFields
        fields.forEach { it.isAccessible = true }
        return fields
    }
}