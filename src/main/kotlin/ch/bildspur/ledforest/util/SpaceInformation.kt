package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeOrigin
import ch.bildspur.model.NumberRange
import ch.bildspur.processing.TransformMatrix
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector

object SpaceInformation {

    data class RangeVector(val x: NumberRange, val y: NumberRange, val z: NumberRange)

    fun calculateTubeDimensions(tubes: List<Tube>): RangeVector {
        val ledPositions = tubes.map { it.leds }
            .map { led ->
                led.map { it.position }
            }.flatten()

        // calculate min & max
        val maxX = ledPositions.maxOfOrNull { it.x } ?: 0f
        val minX = ledPositions.minOfOrNull { it.x } ?: 0f
        val maxY = ledPositions.maxOfOrNull { it.y } ?: 0f
        val minY = ledPositions.minOfOrNull { it.y } ?: 0f
        val maxZ = ledPositions.maxOfOrNull { it.z } ?: 0f
        val minZ = ledPositions.minOfOrNull { it.z } ?: 0f

        return RangeVector(
            NumberRange(minX.toDouble(), maxX.toDouble()),
            NumberRange(minY.toDouble(), maxY.toDouble()),
            NumberRange(minZ.toDouble(), maxZ.toDouble())
        )
    }

    fun calculateLEDPosition(index: Int, tube: Tube): PVector {
        // led size
        val ledHeight = tube.length.value / tube.ledCount.value
        val tubeLength = tube.ledCount.value * ledHeight

        // delta
        val delta = when (tube.origin.value) {
            TubeOrigin.Bottom -> 0f
            TubeOrigin.Center -> -(tubeLength * 0.5f - ledHeight * 0.5f)
            TubeOrigin.Top -> -(tubeLength - ledHeight)
        }

        val position = PVector(0f, 0f, (index * ledHeight) + delta)

        val transform = TransformMatrix(
            translation = tube.position.value,
            rotation = tube.rotation.value
        )

        transform.applyRotationAndTranslation(position)
        return position
    }
}