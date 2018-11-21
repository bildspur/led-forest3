package ch.bildspur.ledforest.leap

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.easing.EasingFloat
import ch.bildspur.ledforest.model.easing.EasingVector
import com.leapmotion.leap.Hand
import processing.core.PVector

class InteractionHand(var hand: Hand,
                      private val interactionBox: PVector,
                      translationSpeed: Float,
                      rotationSpeed: Float) {

    var position = EasingVector(translationSpeed)
    var rotation = EasingVector(rotationSpeed)
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
        val normalizedPosition = PVector((np.x * interactionBox.x) - (interactionBox.x / 2f),
                (np.z * interactionBox.y) - (interactionBox.y / 2f),
                (np.y * interactionBox.z) - (interactionBox.z / 2f))

        return flipVector(normalizedPosition);
    }

    private fun projectedRotation(): PVector {
        val normal = hand.palmNormal()
        return flipVector(PVector(normal.pitch(), -normal.roll(), -normal.yaw()))
    }

    private fun flipVector(vector: PVector): PVector {
        // todo: make it nicer, without global grab!
        val leapSettings = Sketch.instance.project.value.leapInteraction

        val x = if (leapSettings.flipX.value) 1f - vector.x else vector.x
        val y = if (leapSettings.flipY.value) 1f - vector.y else vector.y
        val z = if (leapSettings.flipZ.value) 1f - vector.z else vector.z

        return PVector(x, y, z)
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