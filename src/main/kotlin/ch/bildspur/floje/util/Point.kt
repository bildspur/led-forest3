package ch.bildspur.floje.util

/**
 * Created by cansik on 18.07.17.
 */
open class Point() {
    var x = 0.0
    var y = 0.0

    constructor(x: Double, y: Double) : this() {
        this.x = x
        this.y = y
    }
}