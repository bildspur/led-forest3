package ch.bildspur.ledforest.model.math

import ch.bildspur.math.Float3

class OneEuroFilter3(
        tPrev: Float = 0.0f,
        xPrev: Float3 = Float3(),
        dxPrev: Float3 = Float3(),

        beta: Float = 0.0f,
        minCutoff: Float = 2.0f,
        dCutoff: Float = 1.0f
) {
    private val filterX = OneEuroFilter(tPrev, xPrev.x, dxPrev.x, minCutoff, beta, dCutoff)
    private val filterY = OneEuroFilter(tPrev, xPrev.y, dxPrev.y, minCutoff, beta, dCutoff)
    private val filterZ = OneEuroFilter(tPrev, xPrev.z, dxPrev.z, minCutoff, beta, dCutoff)

    var xPrev: Float3
        get() = Float3(filterX.xPrev, filterY.xPrev, filterZ.xPrev)
        set(value) {
            filterX.xPrev = value.x
            filterY.xPrev = value.y
            filterZ.xPrev = value.z
        }

    var tPrev: Float
        get() = filterX.tPrev
        set(value) {
            filterX.tPrev = value
            filterY.tPrev = value
            filterZ.tPrev = value
        }

    var beta: Float
        get() = filterX.beta
        set(value) {
            filterX.beta = value
            filterY.beta = value
            filterZ.beta = value
        }

    var minCutoff: Float
        get() = filterX.minCutoff
        set(value) {
            filterX.minCutoff = value
            filterY.minCutoff = value
            filterZ.minCutoff = value
        }

    var dCutoff: Float
        get() = filterX.dCutoff
        set(value) {
            filterX.dCutoff = value
            filterY.dCutoff = value
            filterZ.dCutoff = value
        }

    fun filter(t: Float, x: Float3): Float3 {
        return Float3(filterX.filter(t, x.x), filterY.filter(t, x.y), filterZ.filter(t, x.z))
    }

    val value: Float3
        get() = Float3(filterX.value, filterY.value, filterZ.value)
}