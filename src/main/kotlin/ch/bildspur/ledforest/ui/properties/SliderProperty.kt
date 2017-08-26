package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.model.DataModel
import javafx.scene.control.Slider
import java.lang.reflect.Field

class SliderProperty(field: Field, obj: Any, val annotation: SliderParameter) : BaseProperty(field, obj) {
    private val slider = Slider(annotation.minValue, annotation.maxValue, 0.0)

    init {
        children.add(slider)

        val model = field.get(obj) as DataModel<Float>
        model.onChanged += {
            slider.value = model.value.toDouble()
        }
        model.fire()

        slider.valueProperty().addListener { _, _, _ ->
            run {
                model.value = slider.value.toFloat()
                propertyChanged(this)
            }
        }
    }
}