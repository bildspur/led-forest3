package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.ui.control.RelationNumberField
import ch.bildspur.model.DataModel
import ch.bildspur.ui.fx.BaseFXFieldProperty
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

    val model = field.get(obj) as DataModel<PVector>
    val xField = RelationNumberField<Float>(TextFormatter(FloatStringConverter()))
    val yField = RelationNumberField<Float>(TextFormatter(FloatStringConverter()))
    val zField = RelationNumberField<Float>(TextFormatter(FloatStringConverter()))

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

                it.value.setValue(it.value.getValue() + value)
                it.value.fireEvent(ActionEvent())
            }
            label.setOnMouseReleased {
                lastDraggedValue = 0.0
            }

            it.value.prefWidth = RelationNumberField.PREFERRED_WIDTH - 20.0
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
        model.fireLatest()
        children.add(box)
    }
}