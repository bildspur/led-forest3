package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.setup.SetupInformation
import ch.bildspur.ledforest.util.ExtendedRandom
import ch.bildspur.math.radians
import ch.bildspur.processing.TransformMatrix
import processing.core.PApplet
import processing.core.PVector
import kotlin.math.floor
import kotlin.math.sqrt

class StromPattern : SquarePattern("Strøm") {
    val random = ExtendedRandom()

    override fun setupPosition(index: Int, tube: Tube, info: SetupInformation) {
        super.setupPosition(index, tube, info)

        // rotate each tube and move to center
        tube.rotation.value.y = radians(90f)

        // find the x position of the first and last led
        // start - center(start, end)
        var startPos = Sketch.instance.spaceInformation.getLEDPosition(0, tube)
        var endPos = Sketch.instance.spaceInformation.getLEDPosition(tube.ledCount.value - 1, tube)
        tube.position.value = PVector.lerp(startPos, endPos, -0.5f)

        // create basic cascade (front to back)
        val tubeCountPerLine = floor(sqrt(info.tubeCount.toDouble())).toInt()
        val y = if (info.flipXY) index % tubeCountPerLine else index / tubeCountPerLine
        tube.position.value.z += 2.0f - (0.2f * y)

        // add random rotation
        /*
        val rotation = random.randomFloat(-30.0f, 30.0f)
        startPos = Sketch.instance.spaceInformation.getLEDPosition(0, tube)
        endPos = Sketch.instance.spaceInformation.getLEDPosition(tube.ledCount.value - 1, tube)
        val rodVector = PVector.mult(PVector.sub(endPos, startPos), 0.5f)
        val rodTransformed = rodVector.copy()
        val transform = TransformMatrix(PVector(), PVector(0f, radians(rotation),0f))
        transform.apply(rodTransformed)

        rodTransformed.sub(PVector.mult(rodVector, 0.5f))
        tube.position.value.add(rodTransformed)

        tube.rotation.value.y += radians(rotation)
        */
    }
}