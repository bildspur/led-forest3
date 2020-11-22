package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.setup.SetupInformation
import kotlin.math.floor
import kotlin.math.sqrt

open class SquarePattern(name : String = "Square") : BaseClonePattern(name) {

    override fun setupPosition(index: Int, tube: Tube, info: SetupInformation) {
        val tubeCountPerLine = floor(sqrt(info.tubeCount.toDouble())).toInt()

        // check xy flip
        val x = if (info.flipXY) index / tubeCountPerLine else index % tubeCountPerLine
        val y = if (info.flipXY) index % tubeCountPerLine else index / tubeCountPerLine

        tube.position.value.x = (x * info.spaceWidth) - ((tubeCountPerLine - 1) * info.spaceWidth / 2f)
        tube.position.value.y = (y * info.spaceHeight) - ((tubeCountPerLine - 1) * info.spaceHeight / 2f)
    }
}