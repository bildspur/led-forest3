package ch.bildspur.ledforest.interaction

import ch.bildspur.ledforest.model.EasingVector
import com.leapmotion.leap.Hand
import processing.core.PVector

class InteractionHand(var hand: Hand) {
    val interactionBox = PVector(100f, 100f, 100f)

    var position = EasingVector(0.1f)
    var rotation = EasingVector(0.05f)

    fun update() {
        // easing
        position.target = projectedPosition()
        position.update()

        rotation.target = projectedRotation()
        rotation.update()
    }

    private fun projectedPosition(): PVector {
        val np = hand.palmPosition().normalized()
        return PVector(np.x * interactionBox.x,
                np.z * interactionBox.y,
                (np.y * interactionBox.z) - interactionBox.z)
    }

    private fun projectedRotation(): PVector {
        val normal = hand.palmNormal()
        return PVector(normal.pitch(), -normal.roll(), -normal.yaw())
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