package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.configuration.PostProcessable
import ch.bildspur.model.DataModel
import ch.bildspur.processing.TransformMatrix
import ch.bildspur.ui.properties.NumberParameter
import com.google.gson.annotations.Expose
import processing.core.PVector
import kotlin.math.cos
import kotlin.math.sin

class LEDRing : SpatialLightElement(initialLEDCount = 10), PostProcessable {
    init {
        name.value = "LED Ring"
    }

    @Expose
    @NumberParameter("Diameter")
    val diameter = DataModel(1.20f)

    private fun hookDiameterListener() {
        diameter.onChanged += {
            recalculateLEDPosition()
        }
    }

    init {
        hookDiameterListener()
    }

    override val ledLength: Float
        get() = 0.03f

    override fun ledPositionByIndex(index: Int): PVector {
        // this can happen because light element is initialized first
        @Suppress("SENSELESS_COMPARISON")
        if (diameter == null) {
            return PVector()
        }

        val radians = 2 * Math.PI / ledCount.value * index
        val ledPos = PVector(
                (sin(radians) * diameter.value).toFloat(),
                (cos(radians) * diameter.value).toFloat(),
                0f)

        val transform = TransformMatrix(
                translation = position.value,
                rotation = rotation.value
        )

        transform.applyRotationAndTranslation(ledPos)

        return ledPos
    }

    override fun gsonPostProcess() {
        super.gsonPostProcess()
        hookDiameterListener()
    }
}