package ch.bildspur.tracking.simple

data class MinDistance(val index : Int, val distance : Double)

inline fun <S, D> List<S>.matchNearest(destinations :  List<D>,
                                       maximumDistance: Double,
                                       crossinline distance: (S, D) -> Double,
                                       crossinline matched: (D) -> Boolean,
                                       crossinline onMatch: (S, D) -> Unit) {
    // create matrix (source to target)
    val distances = Array(this.size) { DoubleArray(destinations.size) }

    // fill matrix O((m*n)^2)
    this.forEachIndexed { i, source ->
        destinations.forEachIndexed { j, destination ->
            distances[i][j] = distance(source, destination)
        }
    }

    // match best source to target (greedy)
    this.forEachIndexed { i, source ->
        val minDelta = distances[i]
            .mapIndexed { index, distance -> MinDistance(index, distance) }
            .filter { !matched(destinations[it.index]) }
            .minByOrNull { it.distance } ?: MinDistance(-1, Double.MAX_VALUE)

        if (minDelta.distance <= maximumDistance) {
            onMatch(source, destinations[minDelta.index])
        }
    }
}