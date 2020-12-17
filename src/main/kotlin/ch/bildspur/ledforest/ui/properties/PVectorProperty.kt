package ch.bildspur.ledforest.ui.properties

import ch.bildspur.model.DataModel
import ch.bildspur.ui.fx.BaseFXFieldProperty
import ch.bildspur.ui.fx.controls.NumberField
import javafx.event.ActionEvent
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.TextFormatter
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.converter.FloatStringConverter
import processing.core.PVector
import java.lang.reflect.Field

class PVectorProperty(field: Field, obj: Any, val annotation: PVectorParameter) : BaseFXFieldProperty(field, obj) {

    @Suppress("UNCHECKED_CAST")
    val model = field.get(obj) as DataModel<PVector>
    val xField = NumberField<Float>(TextFormatter(FloatStringConverter()))
    val yField = NumberField<Float>(TextFormatter(FloatStringConverter()))
    val zField = NumberField<Float>(TextFormatter(FloatStringConverter()))

    var lastDraggedValue = 0.0

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
            label.cursor = Cursor.H_RESIZE

            label.setOnMouseDragged { event ->
                var value = (lastDraggedValue - event.x) * -1.0
                lastDraggedValue = event.x

                // slow down
                if(event.button == MouseButton.PRIMARY) {
                    value /= 100.0
                }

                if(event.button == MouseButton.SECONDARY) {
                    value /= 10.0
                }

                it.value.value = it.value.value + value
                it.value.fireEvent(ActionEvent())
            }
            label.setOnMouseReleased {
                lastDraggedValue = 0.0
            }

            label.prefWidth = 20.0

            it.value.setOnAction {
                model.value = PVector(
                        xField.value.toFloat(),
                        yField.value.toFloat(),
                        zField.value.toFloat())
                propertyChanged(this)
            }

            box.children.add(HBox(label, it.value))
        }

        // setup binding
        model.onChanged += {
            xField.value = model.value.x.toDouble()
            yField.value = model.value.y.toDouble()
            zField.value = model.value.z.toDouble()
        }
        model.fireLatest()
        children.add(box)
    }
}