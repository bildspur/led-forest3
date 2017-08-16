package ch.bildspur.ledforest.interaction

import com.leapmotion.leap.Hand
import processing.core.PVector

class InteractionHand(val hand: Hand) {
    var easing = 0.1f

    var position: PVector = projectedPosition()
    var target: PVector = projectedPosition()

    fun update() {
        // easing
        if (hand.isValid) {
            target = projectedPosition()
            position.add(PVector.sub(target, position).mult(easing))
        }
    }

    internal fun projectedPosition(): PVector {
        val pos = hand.palmPosition()
        return PVector(pos.x, pos.y, pos.z)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InteractionHand

        if (hand != other.hand) return false

        return true
    }

    override fun hashCode(): Int {
        return hand.hashCode()
    }


}