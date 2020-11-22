package ch.bildspur.processing

import ch.bildspur.ledforest.util.flip
import processing.core.PMatrix3D
import processing.core.PVector
import kotlin.math.cos
import kotlin.math.sin

class TransformMatrix(translation : PVector,
                      rotation : PVector,
                      val flipX : Boolean = false,
                      val flipY : Boolean = false,
                      val flipZ : Boolean = false) {
    private val translationMatrix = PMatrix3D()
    private val rotationMatrix = PMatrix3D()

    init {
        // rotation
        val rotationX = PMatrix3D(
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, cos(rotation.x), -sin(rotation.x), 0.0f,
                0.0f, sin(rotation.x), cos(rotation.x), 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        )

        val rotationY = PMatrix3D(
                cos(rotation.y), 0.0f, sin(rotation.y), 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                -sin(rotation.y), 0.0f, cos(rotation.y), 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        )

        val rotationZ = PMatrix3D(
                cos(rotation.z), -sin(rotation.z), 0.0f, 0.0f,
                sin(rotation.z), cos(rotation.z), 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        )

        rotationX.apply(rotationY)
        rotationX.apply(rotationZ)
        rotationMatrix.set(rotationX)

        // translation
        translationMatrix.set(
                1.0f, 0.0f, 0.0f, translation.x,
                0.0f, 1.0f, 0.0f, translation.y,
                0.0f, 0.0f, 1.0f, translation.z,
                0.0f, 0.0f, 0.0f, 1.0f
        )
    }

    fun apply(vector : PVector) {
        rotationMatrix.mult(vector, vector)
        translationMatrix.mult(vector, vector)

        vector.flip(flipX, flipY, flipZ)
    }

    fun applyTranslationAndRotation(vector : PVector) {
        translationMatrix.mult(vector, vector)
        rotationMatrix.mult(vector, vector)

        vector.flip(flipX, flipY, flipZ)
    }
}