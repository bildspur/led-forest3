package ch.bildspur.ledforest.model.light

import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.NumberParameter
import com.google.gson.annotations.Expose

abstract class LightElement(
        @NumberParameter("Universe") @Expose val universe: DataModel<Int> = DataModel(0)
) {
    val startAddress: Int
        get() = if (leds.isNotEmpty()) leds[0].address else 0

    val endAddress: Int
        get() = if (leds.isNotEmpty()) leds[leds.size - 1].address else 0

    var leds: List<LED> = emptyList()
}