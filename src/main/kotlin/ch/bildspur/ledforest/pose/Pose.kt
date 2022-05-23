package ch.bildspur.ledforest.pose

import ch.bildspur.ledforest.model.easing.EasingVector
import ch.bildspur.ledforest.pose.clients.LightWeightOpenPoseClient

class Pose(
    var id: Int = 0,
    var keypoints: Array<KeyPoint> = Array(LightWeightOpenPoseClient.KEY_POINT_COUNT) { KeyPoint() },
    var score: Float = -1f,
    var startTimestamp: Long = System.currentTimeMillis(),
    var classification: Int = -1
) {

    val easedPosition = EasingVector()

    operator fun get(landmarkType: PoseLandmark): KeyPoint {
        return keypoints[PoseLandmark.values().indexOf(landmarkType)]
    }

    val position: KeyPoint
        get() = neck

    val nose: KeyPoint
        get() = keypoints[0]

    val neck: KeyPoint
        get() = keypoints[1]

    val rightShoulder: KeyPoint
        get() = keypoints[2]

    val rightElbow: KeyPoint
        get() = keypoints[3]

    val rightWrist: KeyPoint
        get() = keypoints[4]

    val leftShoulder: KeyPoint
        get() = keypoints[5]

    val leftElbow: KeyPoint
        get() = keypoints[6]

    val leftWrist: KeyPoint
        get() = keypoints[7]

    val rightHip: KeyPoint
        get() = keypoints[8]

    val rightKnee: KeyPoint
        get() = keypoints[9]

    val rightAnkle: KeyPoint
        get() = keypoints[10]

    val leftHip: KeyPoint
        get() = keypoints[11]

    val leftKnee: KeyPoint
        get() = keypoints[12]

    val leftAnkle: KeyPoint
        get() = keypoints[13]

    val rightEye: KeyPoint
        get() = keypoints[14]

    val leftEye: KeyPoint
        get() = keypoints[15]

    val rightEar: KeyPoint
        get() = keypoints[16]

    val leftEar: KeyPoint
        get() = keypoints[17]

    fun clone(): Pose {
        return Pose(
            id,
            keypoints.map { it.clone() }.toTypedArray(),
            score,
            startTimestamp,
            classification
        )
    }
}