package ch.bildspur.ledforest.configuration.sync

import kotlin.reflect.KClass

data class TypeMapping(
    val deserialize: (data: String) -> Any?,
    val serialize: (data: Any?) -> String = { it.toString() }
)

class ValueMapper {
    val typeMappings = mutableMapOf<KClass<*>, TypeMapping>(
        String::class to TypeMapping({ it }),
        Int::class to TypeMapping({ it.toInt() }),
        Long::class to TypeMapping({ it.toLong() }),
        Float::class to TypeMapping({ it.toFloat() }),
        Double::class to TypeMapping({ it.toDouble() }),
        Boolean::class to TypeMapping({ it.toBoolean() }),
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