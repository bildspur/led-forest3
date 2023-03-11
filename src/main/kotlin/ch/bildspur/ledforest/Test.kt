package ch.bildspur.ledforest

import ch.bildspur.ledforest.configuration.sync.SupabaseConfigSynchronizer
import ch.bildspur.ledforest.model.Project
import ch.bildspur.model.DataModel

var project = DataModel(Project())

class Fruit<T: Any> {
    inline fun sayName(data: T) {
        println(data.toString())
    }
}

fun main() {
    val name = "interaction"
    val data = "false"

    project.value.leda.enabledInteraction.onChanged += {
        println("interaction has changed to: $it")
    }

    val syncer = SupabaseConfigSynchronizer(project)
    syncer.updateValue(name, data)

    syncer.publishValue(name)
}