package ch.bildspur.ledforest.ui.properties.types

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.EnumParameter
import javafx.scene.control.ComboBox
import java.lang.reflect.Field


class EnumProperty(field: Field, obj: Any, val annotation: EnumParameter) : BaseProperty(field, obj) {

    val box = ComboBox<Enum<*>>()

    init {
        // extract enum values
        val model = field.get(obj) as DataModel<Enum<*>>

        val enumObj = model.value
        val classEnum = enumObj::javaClass.get()

        if (classEnum.isEnum) {
            classEnum.enumConstants.forEach {
                box.items.add(it)
            }
        }

        // add binding
        model.onChanged += {
            box.selectionModel.select(model.value)
        }
        model.fireLatest()

        box.setOnAction {
            model.value = box.selectionModel.selectedItem
            propertyChanged(this)
        }

        box.prefWidth = 170.0
        children.add(box)
    }
}