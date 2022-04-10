package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.model.pulse.Pulse
import ch.bildspur.ledforest.pose.PoseLandmark
import com.google.gson.annotations.Expose
import processing.core.PVector
import java.util.*

class LandmarkPulseCollider(@Expose val location: PVector,
                            @Expose val radius: Float,
                            @Expose val triggeredBy: EnumSet<PoseLandmark>,
                            @Expose val pulse: Pulse) : Collider() {

    override fun checkCollision(location: PVector, landmark: PoseLandmark) {
        if (PVector.dist(this.location, location) <= radius && triggeredBy.contains(landmark)) {
            onCollision(Collision(this))
        }
    }
}