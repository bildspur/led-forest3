package ch.bildspur.ledforest.pose

import processing.core.PVector

class Pose {
    var id = 0
    var score: Float = -1f
    var keypoints: Array<PVector> = Array(PoseClient.KEY_POINT_COUNT) { PVector() }
}