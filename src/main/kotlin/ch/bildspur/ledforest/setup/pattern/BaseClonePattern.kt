package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.setup.SetupInformation

abstract class BaseClonePattern(val name: String) : ClonePattern {
    override fun create(project: Project, info: SetupInformation) {

    }

    abstract fun setupPosition(index: Int, tube: Tube, info: SetupInformation)

    override fun toString(): String {
        return name
    }
}