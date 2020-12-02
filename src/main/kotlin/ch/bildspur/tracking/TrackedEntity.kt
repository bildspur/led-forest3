package ch.bildspur.tracking

import ch.bildspur.math.Float2

data class TrackedEntity<T>(
        var item: T,
        var position: Float2 = Float2(),
        var matched: Boolean = false,
        var trackingId: Int = -1,
        var lastMatchTimeStamp: Long = System.currentTimeMillis())
{
    fun getLifeTime(time : Long = System.currentTimeMillis()) : Long {
        return time - lastMatchTimeStamp
    }
}