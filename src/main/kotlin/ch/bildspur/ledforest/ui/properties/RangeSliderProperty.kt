package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.NumberRange
import ch.bildspur.ledforest.util.format
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import org.controlsfx.control.RangeSlider
import java.lang.reflect.Field

class RangeSliderProperty(field: Field, obj: Any, val annotation: RangeSliderParameter) : BaseProperty(field, obj) {
    private val slider = RangeSlider(annotation.minValue, annotation.maxValue, annotation.minValue, annotation.maxValue)
    private val valueLabel = Label()

    val digits = if (annotation.roundInt) 0 else 2

    init {
        slider.majorTickUnit = annotation.majorTick
        slider.minorTickCount = 0
        slider.isSnapToTicks = annotation.snap

        val box = HBox(slider, valueLabel)
        box.spacing = 10.0
        children.add(box)

        val model = field.get(obj) as DataModel<NumberRange>
        model.onChanged += {
            slider.lowValue = model.value.lowValue
            slider.highValue = model.value.highValue
            valueLabel.text = "${model.value.lowValue.format(digits)} - ${model.value.highValue.format(digits)}"
        }
        model.fireLatest()

        slider.lowValueProperty().addListener { _, _, _ ->
            run {
                model.value = NumberRange(slider.lowValue, slider.highValue)
                propertyChanged(this)
            }
        }

        slider.highValueProperty().addListener { _, _, _ ->
            run {
                model.value = NumberRange(slider.lowValue, slider.highValue)
                propertyChanged(this)
            }
        }
    }
}