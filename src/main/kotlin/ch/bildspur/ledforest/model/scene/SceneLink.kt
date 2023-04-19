package ch.bildspur.ledforest.model.scene

import ch.bildspur.ledforest.scene.BaseScene
import ch.bildspur.ledforest.scene.SceneRegistry
import com.google.gson.annotations.Expose

class SceneLink(@Expose val name: String) {
    fun resolve(): BaseScene? {
        val scenes = SceneRegistry.listOfActs().associateBy { it.name }
        return scenes[name]
    }

    override fun toString(): String {
        return name
    }
}