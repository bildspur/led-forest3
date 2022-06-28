package ch.bildspur.ledforest.annotation

import java.lang.reflect.Field

data class AnnotationField<T>(val annotation: Annotation, val field: Field, val entry: T)