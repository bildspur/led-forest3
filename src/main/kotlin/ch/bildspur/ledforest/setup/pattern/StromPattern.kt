package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.setup.SetupInformation
import ch.bildspur.math.radians
import processing.core.PVector
import kotlin.math.floor
import kotlin.math.sqrt

class StromPattern : SquarePattern("Strøm") {

    override fun setupPosition(index: Int, tube: Tube, info: SetupInformation) {
        super.setupPosition(index, tube, info)

        // rotate each tube and move to center
        tube.rotation.value.y = radians(90f)

        // find the x position of the first and last led
        // start - center(start, end)
        val startPos = Sketch.instance.spaceInformation.getLEDPosition(0, tube)
        val endPos = Sketch.instance.spaceInformation.getLEDPosition(tube.ledCount.value - 1, tube)
        tube.position.value = PVector.lerp(startPos, endPos, -0.5f)
    }
}