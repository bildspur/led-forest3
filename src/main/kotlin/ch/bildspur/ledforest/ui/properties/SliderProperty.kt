package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.util.format
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.HBox
import java.lang.reflect.Field

class SliderProperty(field: Field, obj: Any, val annotation: SliderParameter) : BaseProperty(field, obj) {
    private val slider = Slider(annotation.minValue, annotation.maxValue, 0.0)
    private val valueLabel = Label()

    val digits = if (annotation.roundInt) 0 else 2

    init {
        slider.majorTickUnit = annotation.majorTick
        slider.minorTickCount = 0
        slider.isSnapToTicks = annotation.snap

        val box = HBox(slider, valueLabel)
        box.spacing = 10.0
        children.add(box)

        val model = field.get(obj) as DataModel<Number>
        model.onChanged += {
            slider.value = model.value.toDouble()
            valueLabel.text = model.value.format(digits)
        }
        model.fireLatest()

        slider.valueProperty().addListener { _, _, _ ->
            run {
                model.value = slider.value
                propertyChanged(this)
            }
        }
    }
}