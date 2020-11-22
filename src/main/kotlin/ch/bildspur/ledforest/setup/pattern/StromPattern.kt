package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.setup.SetupInformation
import kotlin.math.floor
import kotlin.math.sqrt

class StromPattern : SquarePattern("Strøm") {

    override fun setupPosition(index: Int, tube: Tube, info: SetupInformation) {
        super.setupPosition(index, tube, info)

        // rotate each tube
    }
}