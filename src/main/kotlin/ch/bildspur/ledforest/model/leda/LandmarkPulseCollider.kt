package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.util.*

class LandmarkPulseCollider(
    @Expose @StringParameter("Name") var name: DataModel<String> = DataModel("Collider"),
    @Expose @PVectorParameter("Location") var location: DataModel<PVector> = DataModel(PVector()),
    @Expose @NumberParameter("Radius (m)") var radius: DataModel<Float> = DataModel(1.0f),
    @Expose var triggeredBy: EnumSet<PoseLandmark> = EnumSet.noneOf(PoseLandmark.Nose.javaClass),
    @Expose var pulses: List<Pulse> = mutableListOf(),
    @Expose @BooleanParameter("One Shot") var oneShot: DataModel<Boolean> = DataModel(true)
) : Collider() {

    var hasBeenTriggered = false

    override fun checkCollision(location: PVector, landmark: PoseLandmark): Boolean {
        // very basic sphere collider
        if (PVector.dist(this.location.value, location) <= radius.value && triggeredBy.contains(landmark)) {
            if (oneShot.value && hasBeenTriggered) return false

            // todo: fix has been triggered logic
            // use debounce
            onCollision(Collision(this))
            hasBeenTriggered = true
            return true
        }

        hasBeenTriggered = false
        return false
    }
}