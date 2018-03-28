package ch.bildspur.ledforest.setup

import ch.bildspur.ledforest.setup.pattern.ClonePattern
import ch.bildspur.ledforest.setup.pattern.SquarePattern

class SetupInformation {
    var projectName = "New Project"
    var tubeCount = 16
    var areTubesInverted = false
    var ledsPerTubeCount = 24
    var tubesPerUniverseCount = 4
    var isUniverseAutoFill = false
    var universesPerNode = 4
    var clonePattern: ClonePattern = SquarePattern()
    var space = 50
}