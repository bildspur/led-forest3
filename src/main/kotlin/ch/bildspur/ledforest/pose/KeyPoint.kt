package ch.bildspur.ledforest.pose

import processing.core.PVector

class KeyPoint : PVector {
    var score: Float = 0f
    var velocity: Float = 0f

    constructor() : super()
    constructor(x: Float, y: Float) : super(x, y)
    constructor(x: Float, y: Float, z: Float) : super(x, y, z)
    constructor(x: Float, y: Float, z: Float, score: Float) : super(x, y, z) {
        this.score = score
    }

    constructor(vector: PVector) : super(vector.x, vector.y, vector.z)
    constructor(vector: PVector, score: Float) : super(vector.x, vector.y, vector.z) {
        this.score = score
    }

    companion object {
        fun lerp(v1: KeyPoint, v2: KeyPoint, amt: Float): KeyPoint {
            return KeyPoint(PVector.lerp(v1, v2, amt), (v1.score * (1.0f - amt)) + (v2.score * amt))
        }
    }
}