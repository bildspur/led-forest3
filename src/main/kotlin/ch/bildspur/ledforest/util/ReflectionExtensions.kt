package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.configuration.sync.SyncableAnnotation
import ch.bildspur.model.DataModel
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf

fun KClass<*>.findAllPropertiesWithSyncableAnnotation(): List<KProperty<*>> {
    val properties = memberProperties.filter {
        it.returnType.isSubtypeOf(typeOf<DataModel<*>>()) && it.hasAnnotation<SyncableAnnotation>()
    }

    val subProperties = memberProperties.filter {
        !it.returnType.isSubtypeOf(typeOf<DataModel<*>>())
                && !it.returnType.isSubtypeOf(typeOf<Function<*>>())
                && it.visibility == KVisibility.PUBLIC
    }.mapNotNull { (it.returnType.classifier as? KClass<*>)?.findAllPropertiesWithSyncableAnnotation() }.flatten()

    return properties + subProperties
}