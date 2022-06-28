package ch.bildspur.ledforest.annotation

import java.lang.reflect.Field

data class AnnotationRegistryEntry<T : Annotation, K : Any>(
    val annotation: Class<out Annotation?>?,
    val getRegisteredObject: (field: Field, obj: Any, annotation: T) -> K
)