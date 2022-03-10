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

open class PVectorProperty(field: Field, obj: Any, val annotation: PVectorParameter?) : BaseFXFieldProperty(field, obj) {

    @Suppress("UNCHECKED_CAST")
    val model = field.get(obj) as DataModel<PVector>
    val xField = NumberField<Float>(TextFormatter(FloatStringConverter()))
    val yField = NumberField<Float>(TextFormatter(FloatStringConverter()))
    val zField = NumberField<Float>(TextFormatter(FloatStringConverter()))

    var lastDraggedValue = 0.0

    var slowSpeed = 100.0
    var highSpeed = 10.0

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
                if (event.button == MouseButton.PRIMARY) {
                    value /= slowSpeed
                }

                if (event.button == MouseButton.SECONDARY) {
                    value /= highSpeed
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
                        outMap(xField.value),
                        outMap(yField.value),
                        outMap(zField.value))
                propertyChanged(this)
            }

            box.children.add(HBox(label, it.value))
        }

        // setup binding
        model.onChanged += {
            xField.value = inMap(model.value.x)
            yField.value = inMap(model.value.y)
            zField.value = inMap(model.value.z)
        }
        model.fireLatest()
        children.add(box)
    }

    internal open fun inMap(value: Float): Double {
        return value.toDouble()
    }

    internal open fun outMap(value: Double): Float {
        return value.toFloat()
    }
}