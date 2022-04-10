package ch.bildspur.ledforest.pose.clients

import ch.bildspur.event.Event
import ch.bildspur.ledforest.pose.Pose
import com.illposed.osc.*
import com.illposed.osc.transport.OSCPortIn
import com.illposed.osc.transport.OSCPortInBuilder
import processing.core.PVector
import java.net.InetSocketAddress

class SARMotionPoseClient : OSCPacketListener, PoseClient {
    companion object {
        @JvmStatic
        val KEY_POINT_COUNT = 15
    }

    override val onPosesReceived = Event<MutableList<Pose>>()

    lateinit var server: OSCPortIn

    override fun start(port: Int) {
        val socketAddress = InetSocketAddress("0.0.0.0", port)
        server = OSCPortInBuilder()
                .setSocketAddress(socketAddress)
                .addPacketListener(this)
                .build()
        server.startListening()
    }

    override fun close() {
        server.close()
    }

    override fun handlePacket(event: OSCPacketEvent?) {
        if (event == null) return
        val message = event.packet as OSCMessage

        if (message.address != "/sarmotion/poses")
            return

        var index = 0
        val count = message.arguments[index++] as Int
        if (count == 0) return

        val poses = (0 until count).map {
            val pose = Pose()
            val keypoints = Array(KEY_POINT_COUNT) { PVector() }
            var score = 0f

            pose.id = message.arguments[index++] as Int

            for (i in 0 until KEY_POINT_COUNT) {
                keypoints[i].x = message.arguments[index++] as Float
                keypoints[i].y = message.arguments[index++] as Float
                keypoints[i].z = message.arguments[index++] as Float

                val kpScore = message.arguments[index++] as Float

                // todo: change keypoint system to float4 to include score
                // pose.keypointScores[i] = kpScore

                score += kpScore
            }

            score /= KEY_POINT_COUNT
            pose.score = score

            // ugly mapping to full body pose system
            // sarmotion pose to 15 to 18
            pose.keypoints[0] = keypoints[0]
            pose.keypoints[1] = PVector.lerp(keypoints[3], keypoints[4], 0.5f)

            pose.keypoints[2] = keypoints[4]
            pose.keypoints[3] = keypoints[6]
            pose.keypoints[4] = keypoints[8]

            pose.keypoints[5] = keypoints[3]
            pose.keypoints[6] = keypoints[5]
            pose.keypoints[7] = keypoints[7]

            pose.keypoints[8] = keypoints[10]
            pose.keypoints[9] = keypoints[12]
            pose.keypoints[10] = keypoints[14]

            pose.keypoints[11] = keypoints[9]
            pose.keypoints[12] = keypoints[11]
            pose.keypoints[13] = keypoints[13]

            pose.keypoints[14] = keypoints[2]
            pose.keypoints[15] = keypoints[1]

            // pose.keypoints[16] = keypoints[7]
            // pose.keypoints[17] = keypoints[8]

            pose.easedPosition.set(pose.keypoints[1])

            pose
        }.toMutableList()

        onPosesReceived(poses)
    }

    override fun handleBadData(event: OSCBadDataEvent?) {
        println("bad osc data received!")
    }

}