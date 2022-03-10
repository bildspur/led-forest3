package ch.bildspur.ledforest.pose.clients

enum class PoseClientTypes(val client : PoseClient) {
    LightWeightOpenPose(LightWeightOpenPoseClient()),
    LightWeightOpenPoseProcessing(LightWeightOpenPoseClientP5()),
    MediaPipePose(MediaPipePoseClient()),
    SARMotion(SARMotionPoseClient());
}