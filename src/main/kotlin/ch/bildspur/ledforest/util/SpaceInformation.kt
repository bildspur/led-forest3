package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.light.Tube
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

            // translate height
            it.translate(0f, 0f, (if (tube.inverted.value) -1 else 1) * (index * sketch.project.value.visualisation.ledHeight.value))

            // rotate shape
            it.rotateX(PApplet.radians(90f))

            position.x = it.modelX(0f, 0f, 0f)
            position.y = it.modelY(0f, 0f, 0f)
            position.z = it.modelZ(0f, 0f, 0f)
        }

        return position
    }
}