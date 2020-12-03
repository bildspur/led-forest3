package ch.bildspur.tracking.simple

data class SimplePossibleEntity<T>(
        val item: T,
        var matched: Boolean = false
)
