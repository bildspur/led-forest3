package ch.bildspur.ledforest.ui.properties.types

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.SliderParameter
import ch.bildspur.ledforest.util.format
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.HBox
import java.lang.reflect.Field
import kotlin.math.roundToInt

class SliderProperty(field: Field, obj: Any, val annotation: SliderParameter) : BaseProperty(field, obj) {
    private val slider = Slider(annotation.minValue, annotation.maxValue, 0.0)
    private val valueLabel = Label()

    init {
        val model = field.get(obj) as DataModel<Number>

        slider.majorTickUnit = if (model.value is Int) 1.0 else annotation.majorTick
        slider.minorTickCount = 0
        slider.isSnapToTicks = if (model.value is Int) true else annotation.snap

        val digits = if (model.value is Int || annotation.roundInt) 0 else 2

        val box = HBox(slider, valueLabel)
        box.spacing = 10.0
        children.add(box)

        model.onChanged += {
            slider.value = model.value.toDouble()
            valueLabel.text = model.value.format(digits)
        }
        model.fireLatest()

        slider.valueProperty().addListener { _, _, _ ->
            run {
                if (model.value is Int)
                    model.value = slider.value.roundToInt()

                if (model.value is Float)
                    model.value = slider.value.toFloat()

                if (model.value is Double)
                    model.value = slider.value
                propertyChanged(this)
            }
        }
    }
}