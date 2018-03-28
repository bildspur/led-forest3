package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.setup.SetupInformation

interface ClonePattern {
    fun create(project: Project, info: SetupInformation)
}