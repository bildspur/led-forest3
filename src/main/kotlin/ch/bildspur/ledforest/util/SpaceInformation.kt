package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.NumberRange
import ch.bildspur.ledforest.model.light.Tube
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
        val tubePosition = tubes.map { it.position.value }

        // calculate min & max
        val maxX = tubePosition.map { it.x }.max() ?: 0f
        val minX = tubePosition.map { it.x }.min() ?: 0f
        val maxY = tubePosition.map { it.y }.max() ?: 0f
        val minY = tubePosition.map { it.y }.min() ?: 0f
        val maxZ = tubePosition.map { it.z }.max() ?: 0f
        val minZ = tubePosition.map { it.z }.min() ?: 0f

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