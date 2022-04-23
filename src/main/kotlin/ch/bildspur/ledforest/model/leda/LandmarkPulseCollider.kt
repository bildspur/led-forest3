package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.ledforest.util.DeBouncer
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.util.*
import kotlin.collections.HashSet

class LandmarkPulseCollider(
    @Expose @StringParameter("Name") var name: DataModel<String> = DataModel("Collider"),
    @Expose @PVectorParameter("Location") var location: DataModel<PVector> = DataModel(PVector()),
    @Expose @NumberParameter("Radius (m)") var radius: DataModel<Float> = DataModel(1.0f),
    @Expose var triggeredBy: DataModel<MutableSet<PoseLandmark>> = DataModel(mutableSetOf()),
    @Expose var pulses: List<Pulse> = mutableListOf(),
    @Expose @BooleanParameter("One Shot") var oneShot: DataModel<Boolean> = DataModel(true)
) : Collider() {

    private val deBouncer = DeBouncer(100L, false)

    override fun checkCollision(location: PVector, landmark: PoseLandmark): Boolean {
        // todo: fix debouncer
        // very basic sphere collider
        if (PVector.dist(this.location.value, location) <= radius.value && triggeredBy.value.contains(landmark)) {
            // if (deBouncer.update(true)) return false
            onCollision(Collision(this))
            return true
        } else {
            //deBouncer.update(false)
            return false
        }

        //return deBouncer.currentValue
    }

    override fun toString(): String {
        return "${name.value}"
    }
}