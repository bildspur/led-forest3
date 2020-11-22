package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.setup.SetupInformation

class LinearPattern : BaseClonePattern("Linear") {
    override fun setupPosition(index: Int, tube: Tube, info: SetupInformation) {
        val position = (index * info.spaceWidth) - ((info.tubeCount - 1) * info.spaceWidth / 2f)

        if (info.flipXY)
            tube.position.value.x = position
        else
            tube.position.value.y = position
    }
}