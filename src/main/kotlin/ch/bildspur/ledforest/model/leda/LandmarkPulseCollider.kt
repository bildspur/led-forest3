package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.PoseLandmark
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.util.*

class LandmarkPulseCollider(@Expose var location: PVector = PVector(),
                            @Expose var radius: Float = 1.0f,
                            @Expose var triggeredBy: EnumSet<PoseLandmark> = EnumSet.noneOf(PoseLandmark.Nose.javaClass),
                            @Expose var pulse: Pulse = Pulse(),
                            @Expose var oneShot: Boolean = true) : Collider() {

    var hasBeenTriggered = false

    override fun checkCollision(location: PVector, landmark: PoseLandmark) : Boolean {
        // very basic sphere collider
        if (PVector.dist(this.location, location) <= radius && triggeredBy.contains(landmark)) {
            if (oneShot && hasBeenTriggered) return true

            onCollision(Collision(this))
            hasBeenTriggered = true
            return true
        }

        hasBeenTriggered = false
        return false
    }
}