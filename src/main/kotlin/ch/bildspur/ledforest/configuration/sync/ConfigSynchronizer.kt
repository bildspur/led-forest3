package ch.bildspur.ledforest.configuration.sync

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.util.findAllPropertiesWithSyncableAnnotation
import ch.bildspur.model.DataModel
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

abstract class ConfigSynchronizer(val project: DataModel<Project>) {
    val syncableProperties = Project::class.findAllPropertiesWithSyncableAnnotation()
        .map { it.findAnnotation<SyncableAnnotation>() to it }

    abstract fun start()
}