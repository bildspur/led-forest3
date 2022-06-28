package ch.bildspur.ledforest.annotation

import java.lang.reflect.Field
import java.util.*


@Suppress("UNCHECKED_CAST")
class AnnotationReader(val annotationRegistry: List<AnnotationRegistryEntry<*, *>> = mutableListOf()) {
    private fun readPropertyFields(obj: Any): List<Field> {
        val c = obj.javaClass

        val fields = getDeclaredFields(c).filterNotNull().filter {
            for (annotationEntry in annotationRegistry) {
                annotationEntry as AnnotationRegistryEntry<Annotation, *>
                if (it.isAnnotationPresent(annotationEntry.annotation!!)) {
                    return@filter true
                }
            }
            return@filter false
        }
        fields.forEach { it.isAccessible = true }
        return fields
    }

    private fun getDeclaredFields(clazz: Class<*>): Array<Field?> {
        val fields = clazz.declaredFields
        return if (clazz.superclass != Any::class.java) {
            val pFields = getDeclaredFields(clazz.superclass)
            val allFields = arrayOfNulls<Field>(fields.size + pFields.size)
            Arrays.setAll(allFields) { i: Int -> if (i < pFields.size) pFields[i] else fields[i - pFields.size] }
            allFields
        } else fields
    }

    fun readAnnotations(obj: Any): List<AnnotationField<*>> {
        // todo: make this recursive!
        return readPropertyFields(obj).map {
            // find annotation field
            it to annotationRegistry.find { p -> it.isAnnotationPresent(p.annotation!!) }!! as AnnotationRegistryEntry<Annotation, *>
        }.map { (field, property) ->
            val annotation = field.getAnnotation(property.annotation!!)!!
            AnnotationField(annotation, field, property.getRegisteredObject(field, obj, annotation))
        }
    }
}