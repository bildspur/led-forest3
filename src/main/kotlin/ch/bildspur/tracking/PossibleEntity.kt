package ch.bildspur.tracking

data class PossibleEntity<T>(
        val item: T,
        var matched: Boolean = false
)
