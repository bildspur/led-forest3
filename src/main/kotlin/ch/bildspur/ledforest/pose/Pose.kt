package ch.bildspur.ledforest.pose

import ch.bildspur.math.Float2
import processing.core.PVector

class Pose {
    var id = 0
    var score: Float = -1f
    var keypoints: Array<PVector> = Array(PoseClient.KEY_POINT_COUNT) { PVector() }

    val position : PVector
        get() = neck

    val nose : PVector
        get() = keypoints[0]

    val neck : PVector
        get() = keypoints[1]

    val rightShoulder : PVector
        get() = keypoints[2]

    val rightElbow : PVector
        get() = keypoints[3]

    val rightWrist : PVector
        get() = keypoints[4]

    val leftShoulder : PVector
        get() = keypoints[5]

    val  leftElbow : PVector
        get() = keypoints[6]

    val leftWrist : PVector
        get() = keypoints[7]

    val rightHip : PVector
        get() = keypoints[8]

    val rightKnee : PVector
        get() = keypoints[9]

    val rightAnkle : PVector
        get() = keypoints[10]

    val leftHip : PVector
        get() = keypoints[11]

    val leftKnee : PVector
        get() = keypoints[12]

    val leftAnkle : PVector
        get() = keypoints[13]

    val rightEye : PVector
        get() = keypoints[14]

    val leftEye : PVector
        get() = keypoints[15]

    val rightEar : PVector
        get() = keypoints[16]

    val leftEar : PVector
        get() = keypoints[17]
}