package ch.bildspur.ledforest.model.math

import processing.core.PVector

class PVector4 : PVector {
    var t: Float = 0f

    constructor() : super()
    constructor(x: Float, y: Float) : super(x, y)
    constructor(x: Float, y: Float, z: Float) : super(x, y, z)
    constructor(x: Float, y: Float, z: Float, t: Float) : super(x, y, z) {
        this.t = t
    }

    constructor(vector: PVector) : super(vector.x, vector.y, vector.z)
    constructor(vector: PVector, t: Float) : super(vector.x, vector.y, vector.z) {
        this.t = t
    }

    companion object {
        fun lerp(v1: PVector4, v2: PVector4, amt: Float): PVector4 {
            return PVector4(PVector.lerp(v1, v2, amt), (v1.t * (1.0f - amt)) + (v2.t * amt))
        }
    }
}