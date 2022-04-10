package ch.bildspur.ledforest.model.pulse

import ch.bildspur.ledforest.model.easing.EasingMethod
import processing.core.PVector

class Pulse(
        val startTime: Long,
        val speed: PVector = PVector(1f, 1f, 1f),
        val width: PVector = PVector(1f, 1f, 1f),
        val location: PVector = PVector(),
        val attackCurve: EasingMethod = EasingMethod.Linear,
        val releaseCurve: EasingMethod = EasingMethod.Linear,
) {


    fun getPulseRadius(timesStamp: Long): PVector {
        return PVector.mult(PVector.mult(speed, 0.001f), (timesStamp - startTime).toFloat())
    }
}