package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import processing.core.PVector

abstract class BaseInteractionScene(name: String, project: Project, tubes: List<Tube>) : BaseScene(name, project, tubes) {
    abstract val isInteracting: Boolean

    fun getLEDPosition(index: Int, tube: Tube): PVector {
        return Sketch.instance.spaceInformation.getLEDPosition(index, tube)
    }
}