package ch.bildspur.ledforest.ui.properties.types

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.PVectorAngleParameter
import ch.bildspur.ledforest.util.toDegrees
import ch.bildspur.ledforest.util.toRadians
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import processing.core.PVector
import java.lang.reflect.Field

class PVectorAngleProperty(field: Field, obj: Any, val annotation: PVectorAngleParameter) : BaseProperty(field, obj) {

    private val minAngle = -180.0
    private val maxAngle = 180.0
    private val initValue = 0.0

    val model = field.get(obj) as DataModel<PVector>
    val xField = Slider(minAngle, maxAngle, initValue)
    val yField = Slider(minAngle, maxAngle, initValue)
    val zField = Slider(minAngle, maxAngle, initValue)

    val fields = mapOf(
            Pair("X", xField),
            Pair("Y", yField),
            Pair("Z", zField))

    init {
        val box = VBox()
        box.spacing = 5.0

        // setup fields
        fields.forEach {
            val slider = it.value
            val label = Label("${it.key}:")

            slider.prefWidth = 200.0 - 20.0
            label.prefWidth = 20.0

            slider.isShowTickLabels = true
            slider.isShowTickMarks = true
            slider.majorTickUnit = 90.0

            slider.valueProperty().addListener { _, _, _ ->
                run {
                    model.value = PVector(
                            xField.value.toFloat().toRadians(),
                            yField.value.toFloat().toRadians(),
                            zField.value.toFloat().toRadians())
                }
            }

            slider.setOnMouseClicked({ event ->
                if (event.clickCount == 2) {
                    slider.value = initValue
                }
            })

            box.children.add(HBox(label, it.value))
        }

        // setup binding
        model.onChanged += {
            xField.value = model.value.x.toDegrees().toDouble()
            yField.value = model.value.y.toDegrees().toDouble()
            zField.value = model.value.z.toDegrees().toDouble()
        }
        model.fireLatest()
        children.add(box)
    }
}