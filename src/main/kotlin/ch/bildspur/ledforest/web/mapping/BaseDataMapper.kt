package ch.bildspur.ledforest.web.mapping

import ch.bildspur.model.DataModel
import java.lang.reflect.Field

abstract class BaseDataMapper<T>(val field: Field, val obj: Any, val annotation: Annotation) {
    abstract fun mapIn(value: String): T
    abstract fun mapOut(value: T): String

    fun get(): String {
        val model = field.get(obj) as DataModel<T>
        return mapOut(model.value)
    }

    fun set(data: String) {
        val value = mapIn(data)
        val model = field.get(obj) as DataModel<T>
        model.value = value
    }

    abstract val url : String
}