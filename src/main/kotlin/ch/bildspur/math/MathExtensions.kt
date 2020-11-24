package ch.bildspur.math

import kotlin.math.pow
import kotlin.math.sqrt

fun Float2.distance(v: Float2): Float {
    return sqrt((v.x - this.x).pow(2) + (v.y - this.y).pow(2))
}