package ch.bildspur.ledforest.realsense.tracking

import ch.bildspur.ledforest.model.easing.EasingVector
import org.opencv.core.Point
import processing.core.PApplet
import processing.core.PVector

/**
 * Created by cansik on 12.02.17.
 */
class ActiveRegion(x: Double, y: Double, val area: Double) : Point(x, y) {
    var used = false

    var lifeTime = 0
    var isDead = true

    var normalizedPosition = EasingVector()

    var interactionPosition = PVector()

    fun setCenter(point: Point) {
        this.x = point.x
        this.y = point.y
    }

    constructor(point: Point, area: Double) : this(point.x, point.y, area)

    fun update() {
        normalizedPosition.update()
    }

    fun mapToInteractionBox(box: PVector,
                            flipX: Boolean = false,
                            flipY: Boolean = false,
                            flipZ: Boolean = false) {

        val x = if (flipX) 1f - normalizedPosition.x else normalizedPosition.x
        val y = if (flipY) 1f - normalizedPosition.y else normalizedPosition.y
        val z = if (flipZ) 1f - normalizedPosition.z else normalizedPosition.z

        interactionPosition.x = PApplet.map(x, 0f, 1f, -box.x, box.x)
        interactionPosition.y = PApplet.map(y, 0f, 1f, -box.y, box.y)
        interactionPosition.z = PApplet.map(z, 0f, 1f, -box.z, box.z)
    }
}