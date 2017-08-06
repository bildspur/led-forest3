package ch.bildspur.ledforest.ui.properties

import javafx.scene.control.CheckBox
import java.lang.reflect.Field

class BooleanProperty(field: Field, obj: Any, val annoation: BooleanParameter) : BaseProperty(field, obj) {

    val checkBox = CheckBox()

    init {
        children.add(checkBox)
        checkBox.isSelected = field.get(obj) as Boolean

        checkBox.setOnAction {
            field.set(obj, checkBox.isSelected)
            propertyChanged(this)
        }
    }
}