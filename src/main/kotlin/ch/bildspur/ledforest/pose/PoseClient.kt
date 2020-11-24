package ch.bildspur.ledforest.pose

import ch.bildspur.event.Event
import oscP5.OscMessage

import oscP5.OscP5

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class PoseClient(port: Int) {
    companion object {
        @JvmStatic
        val KEY_POINT_COUNT = 18
    }

    val osc: OscP5
    val poses: MutableList<Pose>

    val onPosesReceived = Event<MutableList<Pose>>()

    private var updatedPoses = AtomicInteger(0)

    init {
        poses = CopyOnWriteArrayList()
        osc = OscP5(this, port)
    }

    fun oscEvent(msg: OscMessage) {
        if (msg.checkAddrPattern("/poses")) {
            preparePoses(msg)
            return
        }
        if (msg.checkAddrPattern("/pose")) {
            updatePose(msg)
            if(updatedPoses.incrementAndGet() >= poses.size) {
                updatedPoses.set(0)
                onPosesReceived(poses)
            }
            return
        }
    }

    private fun preparePoses(msg: OscMessage) {
        val poseCount = msg[0].intValue()
        if (poseCount == poses.size) return
        poses.clear()
        for (i in 0 until poseCount) {
            poses.add(Pose())
        }
        updatedPoses.set(0)
    }

    private fun updatePose(msg: OscMessage) {
        val id = msg[0].intValue()
        val score = msg[1].floatValue()
        val pose = poses[id]
        pose.id = id
        pose.score = score
        for (i in 0 until KEY_POINT_COUNT) {
            val index = 2 + i * 2
            pose.keypoints[i].x = msg[index].floatValue()
            pose.keypoints[i].y = msg[index + 1].floatValue()
        }
    }
}