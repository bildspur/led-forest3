package ch.bildspur.tracking

import ch.bildspur.math.Float2

data class TrackedEntity<T>(
        var item: T,
        var position: Float2 = Float2(),
        var matched: Boolean = false,
        var trackingId: Int = -1,
        var lifeTime: Int = 0
)

data class PossibleEntity<T>(
        val item: T,
        var matched: Boolean = false
)