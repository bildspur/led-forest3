package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube

abstract class BaseInteractionScene(name: String, project: Project, tubes: List<Tube>) : BaseScene(name, project, tubes) {
    abstract val isInteracting: Boolean
}