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
        val ledPositions = tubes.map { Pair(it, it.leds) }
            .map { p ->
                p.second.mapIndexed { i, _ -> getLEDPosition(i, p.first) }
            }.flatten()

        // calculate min & max
        val maxX = ledPositions.map { it.x }.maxOrNull() ?: 0f
        val minX = ledPositions.map { it.x }.minOrNull() ?: 0f
        val maxY = ledPositions.map { it.y }.maxOrNull() ?: 0f
        val minY = ledPositions.map { it.y }.minOrNull() ?: 0f
        val maxZ = ledPositions.map { it.z }.maxOrNull() ?: 0f
        val minZ = ledPositions.map { it.z }.minOrNull() ?: 0f

        return RangeVector(
            NumberRange(minX.toDouble(), maxX.toDouble()),
            NumberRange(minY.toDouble(), maxY.toDouble()),
            NumberRange(minZ.toDouble(), maxZ.toDouble())
        )
    }

    fun getLEDPosition(index: Int, tube: Tube): PVector {
        // led size
        val ledHeight = Sketch.instance.project.value.visualisation.ledHeight.value
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