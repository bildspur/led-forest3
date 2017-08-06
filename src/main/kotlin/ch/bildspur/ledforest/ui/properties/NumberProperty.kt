package ch.bildspur.ledforest.ui.properties

import java.lang.reflect.Field

class NumberProperty(field: Field, obj: Any, val annotation: NumberParameter) : BaseProperty(field, obj) {
}