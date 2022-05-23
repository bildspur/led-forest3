package ch.bildspur.ledforest.ml

import ch.bildspur.ledforest.pose.Pose
import processing.core.PVector
import kotlin.math.max

class PoseEmbedder(val torsoSizeMultiplier: Float = 2.5f) {

    fun create(inputPose: Pose): DoubleArray {
        val pose = inputPose.clone()

        normalizeLandmarks(pose)
        return getPoseDistanceEmbedding(pose)
    }

    private fun normalizeLandmarks(pose: Pose) {
        val poseCenter = getPoseCenter(pose)
        val poseSize = getPoseSize(pose)

        pose.keypoints.forEach {
            it.sub(poseCenter)
            it.div(poseSize)
        }
    }

    private fun getPoseCenter(pose: Pose): PVector {
        return PVector.lerp(pose.leftHip, pose.rightHip, 0.5f)
    }

    private fun getPoseSize(pose: Pose): Float {
        val hipCenter = PVector.lerp(pose.leftHip, pose.rightHip, 0.5f)
        val shoulderCenter = PVector.lerp(pose.leftShoulder, pose.rightShoulder, 0.5f)

        val torsoSize = PVector.sub(shoulderCenter, hipCenter).mag()

        val poseCenter = getPoseCenter(pose)
        val maxDistance = pose.keypoints.maxOf { PVector.dist(it, poseCenter) }

        return max(torsoSize * torsoSizeMultiplier, maxDistance)
    }

    private fun getPoseDistanceEmbedding(pose: Pose): DoubleArray {
        val hipCenter = PVector.lerp(pose.leftHip, pose.rightHip, 0.5f)
        val shoulderCenter = PVector.lerp(pose.leftShoulder, pose.rightShoulder, 0.5f)

        return arrayOf(
            // one joint
            PVector.dist(hipCenter, shoulderCenter).toDouble(),

            PVector.dist(pose.leftShoulder, pose.leftElbow).toDouble(),
            PVector.dist(pose.rightShoulder, pose.rightElbow).toDouble(),

            PVector.dist(pose.leftElbow, pose.leftWrist).toDouble(),
            PVector.dist(pose.rightElbow, pose.rightWrist).toDouble(),

            PVector.dist(pose.leftHip, pose.leftKnee).toDouble(),
            PVector.dist(pose.rightHip, pose.rightKnee).toDouble(),

            PVector.dist(pose.leftKnee, pose.leftAnkle).toDouble(),
            PVector.dist(pose.rightKnee, pose.rightAnkle).toDouble(),

            // two joints
            PVector.dist(pose.leftShoulder, pose.leftWrist).toDouble(),
            PVector.dist(pose.rightShoulder, pose.rightWrist).toDouble(),

            PVector.dist(pose.leftHip, pose.leftAnkle).toDouble(),
            PVector.dist(pose.rightHip, pose.rightAnkle).toDouble(),

            // four joints
            PVector.dist(pose.leftHip, pose.leftWrist).toDouble(),
            PVector.dist(pose.rightHip, pose.rightWrist).toDouble(),

            // five joints
            PVector.dist(pose.leftShoulder, pose.leftAnkle).toDouble(),
            PVector.dist(pose.rightShoulder, pose.rightAnkle).toDouble(),

            // cross body
            PVector.dist(pose.leftElbow, pose.rightElbow).toDouble(),
            PVector.dist(pose.leftKnee, pose.rightKnee).toDouble(),

            PVector.dist(pose.leftWrist, pose.rightWrist).toDouble(),
            PVector.dist(pose.leftAnkle, pose.rightAnkle).toDouble(),
        ).toDoubleArray()
    }
}