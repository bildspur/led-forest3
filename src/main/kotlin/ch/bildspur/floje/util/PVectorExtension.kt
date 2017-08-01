package ch.bildspur.floje.util

import processing.core.PApplet
import processing.core.PVector


fun PVector.rotationInX(v: PVector): Float {
    val delta = PVector.sub(this, v)
    return PApplet.degrees(PApplet.atan(delta.x / delta.z))
}

fun PVector.rotationInY(v: PVector): Float {
    val delta = PVector.sub(this, v)
    return PApplet.degrees(PApplet.atan(delta.z / delta.y))
}

fun PVector.rotationInZ(v: PVector): Float {
    val delta = PVector.sub(this, v)
    return PApplet.degrees(PApplet.atan(delta.y / delta.x))
}

fun PVector.rotationInAxis(v: PVector): PVector {
    return PVector(this.rotationInX(v), this.rotationInY(v), this.rotationInZ(v))
}

fun PVector.add(s: Float) {
    this.x += s
    this.y += s
    this.z += s
}

fun PVector.toPolar(): PolarCoordinates {
    val r = PApplet.sqrt(PApplet.pow(x, 2f) + PApplet.pow(y, 2f))
    var theta = PApplet.degrees(PApplet.atan(y / x))

    if (x > 0 && y > 0)
        theta += 0f

    if (x < 0 && y > 0)
        theta += 180f

    if (x < 0 && y < 0)
        theta += 180f

    if (x > 0 && y < 0)
        theta += 360f

    return PolarCoordinates(r, theta)
}