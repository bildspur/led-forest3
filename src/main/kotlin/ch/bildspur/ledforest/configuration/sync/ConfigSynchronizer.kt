package ch.bildspur.ledforest.configuration.sync

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.util.findAllPropertiesWithSyncableAnnotationRelative
import ch.bildspur.model.DataModel
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

private val _dataModelValueProperty =
    DataModel::class.memberProperties.first { it.name == "value" } as KMutableProperty<*>

data class SyncableProperty(
    val annotation: SyncableAnnotation,
    val property: KProperty<DataModel<*>>,
    val instance: DataModel<*>
) {
    val modelType: KType
        get() = property.returnType.arguments[0].type!!
}

abstract class ConfigSynchronizer(val project: DataModel<Project>) {
    lateinit var syncableProperties: Map<String, SyncableProperty>
    val valueMapper = ValueMapper()

    init {
        project.onChanged += {
            syncableProperties = Project::class.findAllPropertiesWithSyncableAnnotationRelative(project.value)
                .map {
                    SyncableProperty(
                        it.property.findAnnotation()!!,
                        it.property as KProperty<DataModel<*>>,
                        it.instance as DataModel<*>
                    )
                }
                .associateBy { it.annotation.key }
        }
        project.fireLatest()
    }

    abstract fun start()

    fun update(key: String, data: String) {
        val property = syncableProperties[key] ?: return

        // convert data value into format
        val value = valueMapper.deserialize(property.modelType, data)
        _dataModelValueProperty.setter.call(property.instance, value)
    }
}