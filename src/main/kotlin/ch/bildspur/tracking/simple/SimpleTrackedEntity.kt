package ch.bildspur.tracking.simple

import ch.bildspur.math.Float2
import ch.bildspur.tracking.TrackedEntity

class SimpleTrackedEntity<T>(item: T,
                             var position: Float2 = Float2(),
                             var matched: Boolean = false,
                             var trackingId: Int = -1,
                             var lastMatchTimeStamp: Long = System.currentTimeMillis()) : TrackedEntity<T>(item) {

    fun getLifeTime(time: Long = System.currentTimeMillis()): Long {
        return time - lastMatchTimeStamp
    }

    fun predictNextPosition() {

    }
}