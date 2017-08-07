package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.model.DataModel
import javafx.scene.control.CheckBox
import java.lang.reflect.Field

class BooleanProperty(field: Field, obj: Any, val annoation: BooleanParameter) : BaseProperty(field, obj) {

    val checkBox = CheckBox()

    init {
        children.add(checkBox)

        val model = field.get(obj) as DataModel<Boolean>
        model.onChanged += {
            checkBox.isSelected = model.value
        }

        // set value
        model.fire()

        checkBox.setOnAction {
            model.value = checkBox.isSelected
            propertyChanged(this)
        }
    }
}