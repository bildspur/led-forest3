package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.scene.SceneLink
import com.google.gson.annotations.Expose

abstract class BaseScene(@Expose val name: String, val project: Project, val tubes: List<Tube>) {
    abstract val timerTask: TimerTask

    abstract fun setup()
    abstract fun update()
    abstract fun stop()
    abstract fun dispose()

    override fun toString(): String {
        return name
    }

    fun asLink(): SceneLink {
        return SceneLink(name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseScene) return false

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}