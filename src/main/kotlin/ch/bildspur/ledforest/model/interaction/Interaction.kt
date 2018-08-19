package ch.bildspur.ledforest.model.interaction

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.ActionParameter
import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import com.google.gson.annotations.Expose
import processing.core.PVector

class Interaction {
    @Expose
    @BooleanParameter("Interaction Data Enabled")
    var isInteractionDataEnabled = DataModel(true)

    @Expose
    @BooleanParameter("Leap Interaction Enabled")
    var isLeapInteractionEnabled = DataModel(false)

    @Expose
    @BooleanParameter("Real Sense Interaction Enabled")
    var isRealSenseInteractionEnabled = DataModel(false)

    @Expose
    @PVectorParameter("Interaction Box")
    var interactionBox = DataModel(PVector(15f, 15f, 10f))

    @ActionParameter("Interaction Box", "Auto Scale")
    val autoScaleInteractionBox = {
        val tubes = Sketch.instance.project.value.tubes.map { it.position.value }
        val scaleFactor = 2.2

        // calculate min & max
        val maxX = tubes.map { it.x }.max() ?: 0f
        val minX = tubes.map { it.x }.min() ?: 0f
        val maxY = tubes.map { it.y }.max() ?: 0f
        val minY = tubes.map { it.y }.min() ?: 0f
        val maxZ = tubes.map { it.z }.max() ?: 0f
        val minZ = tubes.map { it.z }.min() ?: 0f

        interactionBox.value = PVector(
                Math.ceil(Math.max(Math.abs(minX), maxX) * scaleFactor).toFloat(),
                Math.ceil(Math.max(Math.abs(minY), maxY) * scaleFactor).toFloat(),
                Math.ceil(Math.max(Math.abs(minZ), maxZ) * scaleFactor).toFloat()
        )
    }


    @Expose
    @BooleanParameter("Show Interaction Box")
    var showInteractionInfo = DataModel(false)
}