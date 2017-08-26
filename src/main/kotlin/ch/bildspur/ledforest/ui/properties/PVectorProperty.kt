package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.control.RelationNumberField
import javafx.scene.control.Label
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.converter.FloatStringConverter
import processing.core.PVector
import java.lang.reflect.Field

class PVectorProperty(field: Field, obj: Any, val annotation: PVectorParameter) : BaseProperty(field, obj) {

    val model = field.get(obj) as DataModel<PVector>
    val xField = RelationNumberField<Float>(TextFormatter(FloatStringConverter()))
    val yField = RelationNumberField<Float>(TextFormatter(FloatStringConverter()))
    val zField = RelationNumberField<Float>(TextFormatter(FloatStringConverter()))

    val fields = mapOf(
            Pair("X", xField),
            Pair("Y", yField),
            Pair("Z", zField))

    init {
        val box = VBox()
        box.spacing = 5.0

        // setup fields
        fields.forEach {
            val label = Label("${it.key}:")

            it.value.prefWidth = 200.0 - 20.0
            it.value.isShowRange = false
            label.prefWidth = 20.0

            it.value.setOnAction {
                model.value = PVector(
                        xField.getValue().toFloat(),
                        yField.getValue().toFloat(),
                        zField.getValue().toFloat())
            }

            box.children.add(HBox(label, it.value))
        }

        // setup binding
        model.onChanged += {
            xField.setValue(model.value.x.toDouble())
            yField.setValue(model.value.y.toDouble())
            zField.setValue(model.value.z.toDouble())

        }
        model.fire()
        children.add(box)
    }
}