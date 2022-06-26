package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.PoseLandmark
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.ledforest.util.Debouncer
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.EnumParameter
import ch.bildspur.ui.properties.NumberParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import processing.core.PVector

class LandmarkPulseCollider(
    @Expose @StringParameter("Name") var name: DataModel<String> = DataModel("Collider"),
    @Expose @PVectorParameter("Location") var location: DataModel<PVector> = DataModel(PVector()),
    @Expose @NumberParameter("Radius (m)") var radius: DataModel<Float> = DataModel(1.0f),
    @Expose @EnumParameter("Trigger Logic") var triggerLogic: DataModel<TriggerLogic> = DataModel(TriggerLogic.Any),
    @Expose var triggeredBy: DataModel<MutableSet<PoseLandmark>> = DataModel(mutableSetOf()),
    @Expose var pulses: List<Pulse> = mutableListOf(),
    @Expose @BooleanParameter("One Shot") var oneShot: DataModel<Boolean> = DataModel(true)
) : Collider() {

    private val debouncer = Debouncer(25L, false)
    private var currentState = ColliderState.Inactive

    val state: ColliderState
        get() = currentState

    override fun checkCollision(collisionCandidates: List<CollisionCandidate>): Boolean {
        val collisionHappened = when (triggerLogic.value) {
            TriggerLogic.All -> collisionCandidates.all { collide(it) }
            TriggerLogic.Any -> collisionCandidates.any { collide(it) }
        }

        // very basic sphere collider
        if (collisionHappened) {
            if (debouncer.update(true)) {
                if (oneShot.value && currentState == ColliderState.Active) {
                    return false
                }

                currentState = ColliderState.Active
                onCollision(Collision(this))

                return true
            }
        } else {
            if (!debouncer.update(false)) {
                currentState = ColliderState.Inactive
            }
        }

        return false
    }

    private fun collide(candidate: CollisionCandidate): Boolean {
        return PVector.dist(this.location.value, candidate.location) <= radius.value
                && triggeredBy.value.contains(candidate.landmark)
    }

    override fun toString(): String {
        return name.value
    }
}