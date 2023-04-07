package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.configuration.PostProcessable
import ch.bildspur.processing.TransformMatrix
import processing.core.PVector

class LEDSpot : SpatialLightElement(initialLEDCount = 1), PostProcessable {
    init {
        name.value = "LED Spot"
    }

    override val ledSize: PVector
        get() = PVector(0.25f, 0.25f,0.3f)

    override fun ledPositionByIndex(index: Int): PVector {
        val ledPos = PVector(0f, 0f, 0f)

        // this can happen because light element is initialized first
        @Suppress("SENSELESS_COMPARISON")
        if (position == null || rotation == null) {
            return PVector()
        }


        val transform = TransformMatrix(
            translation = position.value,
            rotation = rotation.value
        )

        transform.applyRotationAndTranslation(ledPos)

        return ledPos
    }
}