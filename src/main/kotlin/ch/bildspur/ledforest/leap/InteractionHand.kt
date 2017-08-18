package ch.bildspur.ledforest.leap

import ch.bildspur.ledforest.model.easing.EasingFloat
import ch.bildspur.ledforest.model.easing.EasingVector
import com.leapmotion.leap.Hand
import processing.core.PVector

class InteractionHand(var hand: Hand) {
    private val box = PVector(150f, 150f, 100f)

    var position = EasingVector(0.1f)
    var rotation = EasingVector(0.05f)
    var grabStrength = EasingFloat(0.1f)

    fun update() {
        // easing
        position.target = projectedPosition()
        position.update()

        rotation.target = projectedRotation()
        rotation.update()

        grabStrength.target = hand.grabStrength()
        grabStrength.update()
    }

    private fun projectedPosition(): PVector {
        val np = hand.frame().interactionBox().normalizePoint(hand.palmPosition(), true)
        return PVector((np.x * box.x) - (box.x / 2f),
                (np.z * box.y) - (box.y / 2f),
                (np.y * box.z) - (box.z / 2f))
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