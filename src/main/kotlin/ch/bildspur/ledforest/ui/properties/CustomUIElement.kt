package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ui.fx.BaseFXFieldProperty
import ch.bildspur.ui.properties.PropertyComponent
import java.lang.reflect.Field

@Suppress("UNCHECKED_CAST")
class CustomUIElement(field: Field, obj: Any, annotation: CustomUIParameter) : BaseFXFieldProperty(field, obj), PropertyComponent {

    init {
        val initBlock = field.get(obj) as ((BaseFXFieldProperty) -> Unit)
        initBlock(this)
    }
}