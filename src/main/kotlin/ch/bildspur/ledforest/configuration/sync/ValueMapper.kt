package ch.bildspur.ledforest.configuration.sync

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

data class TypeMapping<T : Any>(
    val serialize: (data: T) -> String,
    val deserialize: (data: String) -> T,
)

class ValueMapper {
    val mappers = mutableMapOf<KClass<*>, TypeMapping<*>>(
        Boolean::class to TypeMapping({ if (it) "true" else "false" }, { it.toBoolean() })
    )

    fun serialize(type: KType, data: Any?): String {
        val mapper = mappers[type.jvmErasure] ?: return ""
        return mapper.serialize(data as Nothing)
    }

    fun deserialize(type: KType, data: String): Any? {
        val mapper = mappers[type.jvmErasure] ?: return null
        return mapper.deserialize(data)
    }
}