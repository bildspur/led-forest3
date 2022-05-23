package ch.bildspur.ledforest.model.math

import ch.bildspur.math.Float2

class OneEuroFilter2(
    tPrev: Float = 0.0f,
    xPrev: Float2 = Float2(),
    dxPrev: Float2 = Float2(),

    beta: Float = 0.0f,
    minCutoff: Float = 2.0f,
    dCutoff: Float = 1.0f
) {
    private val filterX = OneEuroFilter(tPrev, xPrev.x, dxPrev.x, minCutoff, beta, dCutoff)
    private val filterY = OneEuroFilter(tPrev, xPrev.y, dxPrev.y, minCutoff, beta, dCutoff)

    var xPrev: Float2
        get() = Float2(filterX.xPrev, filterY.xPrev)
        set(value) {
            filterX.xPrev = value.x
            filterY.xPrev = value.y
        }

    var tPrev: Float
        get() = filterX.tPrev
        set(value) {
            filterX.tPrev = value
            filterY.tPrev = value
        }

    var beta: Float
        get() = filterX.beta
        set(value) {
            filterX.beta = value
            filterY.beta = value
        }

    var minCutoff: Float
        get() = filterX.minCutoff
        set(value) {
            filterX.minCutoff = value
            filterY.minCutoff = value
        }

    var dCutoff: Float
        get() = filterX.dCutoff
        set(value) {
            filterX.dCutoff = value
            filterY.dCutoff = value
        }

    fun filter(t: Float, x: Float2): Float2 {
        return Float2(filterX.filter(t, x.x), filterY.filter(t, x.y))
    }
}