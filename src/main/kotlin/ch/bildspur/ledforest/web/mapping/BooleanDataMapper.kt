package ch.bildspur.ledforest.web.mapping

import ch.bildspur.ledforest.web.BooleanWebEndpoint
import java.lang.reflect.Field

class BooleanDataMapper(field: Field, obj: Any, annotation: Annotation) :
    BaseDataMapper<Boolean>(field, obj, annotation) {
    override fun mapIn(value: String): Boolean {
        return (value == "1")
    }

    override fun mapOut(value: Boolean): String {
        return if (value) {
            "1"
        } else {
            "0"
        }
    }

    override val url: String
        get() = (annotation as BooleanWebEndpoint).url
}