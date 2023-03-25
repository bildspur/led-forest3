package ch.bildspur.ledforest.configuration.sync

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.util.findAllPropertiesWithSyncableAnnotationRelative
import ch.bildspur.model.DataModel
import javafx.application.Platform
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

private val _dataModelValueProperty =
    DataModel::class.memberProperties.first { it.name == "value" } as KMutableProperty<*>

data class SyncableProperty(
    val annotation: ApiExposed,
    val property: KProperty<DataModel<*>>,
    val instance: DataModel<*>,
    var suppressed: Boolean = false
) {
    val modelType: KType
        get() = property.returnType.arguments[0].type!!

    val dataType: KClass<*>
        get() = modelType.jvmErasure
}

abstract class ConfigSynchronizer(val project: DataModel<Project>) {
    lateinit var syncableProperties: Map<String, SyncableProperty>
    val valueMapper = ValueMapper()

    init {
        project.onChanged += {
            // get syncable properties
            syncableProperties = Project::class.findAllPropertiesWithSyncableAnnotationRelative(project.value)
                .map {
                    SyncableProperty(
                        it.property.findAnnotation()!!,
                        it.property as KProperty<DataModel<*>>,
                        it.instance as DataModel<*>
                    )
                }
                .associateBy { it.annotation.key }

            // add event handler for each property
            syncableProperties.filter { it.value.annotation.publish }.forEach { (k, a) ->
                println("adding publish handler to $k")
                a.instance.onChanged += {
                    onValuePublish(k)
                }
            }
        }
        project.fireLatest()
    }

    abstract fun start()

    protected abstract fun publishValue(key: String, value: Any?, data: String)

    fun onValueReceived(key: String, data: String) {
        val property = syncableProperties[key]

        if (property == null) {
            println("Property $key is not registered for synchronization.")
            return
        }

        if (!property.annotation.receive) {
            println("Property $key is not set to receive data.")
            return
        }

        // convert data value into format
        var value: Any? = null
        val type = property.modelType.jvmErasure

        try {
            value = valueMapper.deserialize(property.modelType.jvmErasure, data)
        } catch (ex: Exception) {
            println("Sync Error: Could not deserialize $key ($type) $data")
            return
        }

        // only update value if necessary
        if (property.instance.value == value)
            return

        Platform.runLater {
            property.suppressed = true
            _dataModelValueProperty.setter.call(property.instance, value)
            property.suppressed = false
        }
    }

    fun onValuePublish(key: String) {
        val property = syncableProperties[key]

        if (property == null) {
            println("Property $key is not registered for synchronization.")
            return
        }

        if (!property.annotation.publish) {
            println("Property $key is not set to publish data.")
            return
        }

        if (property.suppressed) {
            return
        }

        val value = property.instance.value
        val type = property.modelType.jvmErasure

        try {
            val data = valueMapper.serialize(property.modelType.jvmErasure, value)
            publishValue(key, value, data)
        } catch (ex: Exception) {
            println("Sync Error: Could not serialize $key ($type): $value")
            return
        }
    }
}