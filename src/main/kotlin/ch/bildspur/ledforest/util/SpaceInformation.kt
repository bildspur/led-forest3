package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeOrigin
import ch.bildspur.model.NumberRange
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PVector

class SpaceInformation(val sketch: Sketch) {
    private lateinit var space: PGraphics

    data class RangeVector(val x: NumberRange, val y: NumberRange, val z: NumberRange)

    fun setup() {
        space = sketch.createGraphics(10, 10, PApplet.P3D)
    }

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

        return RangeVector(NumberRange(minX.toDouble(), maxX.toDouble()),
                NumberRange(minY.toDouble(), maxY.toDouble()),
                NumberRange(minZ.toDouble(), maxZ.toDouble()))
    }

    fun getLEDPosition(index: Int, tube: Tube): PVector {
        val position = PVector()
        space.stackMatrix {
            // translate normalizedPosition
            it.translate(tube.position.value)

            // global rotation
            it.rotateX(tube.rotation.value.x)
            it.rotateY(tube.rotation.value.y)
            it.rotateZ(tube.rotation.value.z)

            // led size
            val ledHeight = sketch.project.value.visualisation.ledHeight.value
            val tubeLength = tube.ledCount.value * ledHeight

            // delta
            val delta = when (tube.origin.value) {
                TubeOrigin.Bottom -> 0f
                TubeOrigin.Center -> -(tubeLength * 0.5f - ledHeight * 0.5f)
                TubeOrigin.Top -> -(tubeLength - ledHeight)
            }

            // translate height
            it.translate(0f, 0f, (index * ledHeight) + delta)

            // rotate shape
            it.rotateX(PApplet.radians(90f))

            position.x = it.modelX(0f, 0f, 0f)
            position.y = it.modelY(0f, 0f, 0f)
            position.z = it.modelZ(0f, 0f, 0f)
        }

        return position
    }
}