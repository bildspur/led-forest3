package ch.bildspur.ledforest.configuration.sync

import kotlin.reflect.KClass

data class TypeMapping(
    val serialize: (data: Any?) -> String,
    val deserialize: (data: String) -> Any?,
)

class ValueMapper {
    val typeMappings = mutableMapOf<KClass<*>, TypeMapping>(
        Boolean::class to TypeMapping({ if (it as Boolean) "true" else "false" }, { it.toBoolean() })
    )

    fun serialize(type: KClass<*>, data: Any?): String {
        val mapper = typeMappings[type] ?: return ""
        return mapper.serialize(data)
    }

    fun deserialize(type: KClass<*>, data: String): Any? {
        val mapper = typeMappings[type] ?: return null
        return mapper.deserialize(data)
    }
}