package ch.bildspur.ledforest.realsense.util

import org.opencv.core.Point

/**
 * Created by cansik on 06.01.17.
 */
fun <T : Point> MutableList<T>.sparsePoints(maxDistance: Double): MutableList<MutableList<T>> {
    val sparseCloud = mutableListOf<MutableList<T>>()
    val points = this.toMutableList()

    while (!points.isEmpty()) {
        val firstPoint = points.removeAt(0)
        val group = mutableListOf<T>()
        val nearPoints = mutableListOf(firstPoint)

        while (!nearPoints.isEmpty()) {
            val point = nearPoints.removeAt(0)
            group.add(point)

            for (p in points.toList()) {
                // calculate distance
                val dist = point.distance(p)

                if (dist < maxDistance) {
                    points.remove(p)
                    nearPoints.add(p)
                }
            }
        }

        sparseCloud.add(group)
    }

    return sparseCloud
}

fun <T : Point> MutableList<MutableList<T>>.combinePoints(): MutableList<Point> {
    val sparsePoints = mutableListOf<Point>()
    this.mapTo(sparsePoints) { pointList -> Point(pointList.map { it.x }.average(), pointList.map { it.y }.average()) }
    return sparsePoints
}

fun Point.distance(p2: Point): Double {
    return Math.sqrt(Math.pow(p2.x - this.x, 2.0) + Math.pow(p2.y - this.y, 2.0))
}