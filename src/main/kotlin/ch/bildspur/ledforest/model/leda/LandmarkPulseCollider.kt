package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.ledforest.util.Debouncer
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import processing.core.PVector

class LandmarkPulseCollider(
        @Expose @StringParameter("Name") var name: DataModel<String> = DataModel("Collider"),
        @Expose @PVectorParameter("Location") var location: DataModel<PVector> = DataModel(PVector()),
        @Expose @NumberParameter("Radius (m)") var radius: DataModel<Float> = DataModel(1.0f),
        @Expose var triggeredBy: DataModel<MutableSet<PoseLandmark>> = DataModel(mutableSetOf()),
        @Expose var pulses: List<Pulse> = mutableListOf(),
        @Expose @BooleanParameter("One Shot") var oneShot: DataModel<Boolean> = DataModel(true)
) : Collider() {

    private val debouncer = Debouncer(100L, false)
    private var currentState = ColliderState.Inactive

    override fun checkCollision(location: PVector, landmark: PoseLandmark): Boolean {
        // very basic sphere collider
        if (PVector.dist(this.location.value, location) <= radius.value && triggeredBy.value.contains(landmark)) {
            if (debouncer.update(true)) {
                if (currentState == ColliderState.Inactive || !oneShot.value) {
                    currentState = ColliderState.Active
                    onCollision(Collision(this))
                }
            }
            currentState
        } else {
            if (!debouncer.update(false)) {
                if (currentState == ColliderState.Active || !oneShot.value) {
                    currentState = ColliderState.Inactive
                }
            }
        }

        return currentState == ColliderState.Active
    }

    override fun toString(): String {
        return "${name.value}"
    }
}