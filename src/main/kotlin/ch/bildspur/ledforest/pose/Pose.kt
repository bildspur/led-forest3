package ch.bildspur.ledforest.pose

import ch.bildspur.ledforest.model.easing.EasingVector
import ch.bildspur.ledforest.model.math.PVector4
import ch.bildspur.ledforest.pose.clients.LightWeightOpenPoseClient

class Pose {
    var id = 0
    var score: Float = -1f

    var keypoints: Array<PVector4> = Array(LightWeightOpenPoseClient.KEY_POINT_COUNT) { PVector4() }

    var startTimestamp = System.currentTimeMillis()
    val easedPosition = EasingVector()

    operator fun get(landmarkType: PoseLandmark): PVector4 {
        return keypoints[PoseLandmark.values().indexOf(landmarkType)]
    }

    val position : PVector4
        get() = neck

    val nose : PVector4
        get() = keypoints[0]

    val neck : PVector4
        get() = keypoints[1]

    val rightShoulder : PVector4
        get() = keypoints[2]

    val rightElbow : PVector4
        get() = keypoints[3]

    val rightWrist : PVector4
        get() = keypoints[4]

    val leftShoulder : PVector4
        get() = keypoints[5]

    val  leftElbow : PVector4
        get() = keypoints[6]

    val leftWrist : PVector4
        get() = keypoints[7]

    val rightHip : PVector4
        get() = keypoints[8]

    val rightKnee : PVector4
        get() = keypoints[9]

    val rightAnkle : PVector4
        get() = keypoints[10]

    val leftHip : PVector4
        get() = keypoints[11]

    val leftKnee : PVector4
        get() = keypoints[12]

    val leftAnkle : PVector4
        get() = keypoints[13]

    val rightEye : PVector4
        get() = keypoints[14]

    val leftEye : PVector4
        get() = keypoints[15]

    val rightEar : PVector4
        get() = keypoints[16]

    val leftEar : PVector4
        get() = keypoints[17]
}