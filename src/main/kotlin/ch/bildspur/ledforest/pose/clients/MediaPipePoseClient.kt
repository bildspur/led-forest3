package ch.bildspur.ledforest.pose.clients

import ch.bildspur.event.Event
import ch.bildspur.ledforest.pose.Pose
import com.illposed.osc.*
import com.illposed.osc.transport.udp.OSCPortIn
import com.illposed.osc.transport.udp.OSCPortInBuilder
import processing.core.PVector
import java.net.InetSocketAddress

class MediaPipePoseClient : OSCPacketListener, PoseClient {
    companion object {
        @JvmStatic
        val KEY_POINT_COUNT = 25
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

        if (message.address != "/mediapipe/pose")
            return

        val count = message.arguments[0] as Int
        if (count == 0) return

        val pose = Pose()
        val keypoints = Array(KEY_POINT_COUNT) { PVector() }
        var score = 0f

        for (i in 0 until KEY_POINT_COUNT) {
            var index = 1 + i * 4

            keypoints[i].x = message.arguments[index++] as Float
            keypoints[i].y = message.arguments[index++] as Float
            keypoints[i].z = message.arguments[index++] as Float

            val visibility = message.arguments[index] as Float
            score += visibility
        }

        score /= KEY_POINT_COUNT
        pose.score = score

        // ugly mapping to full body pose system
        // upper pose 25 to coco 18
        pose.keypoints[0] = keypoints[0]
        pose.keypoints[1] = PVector.lerp(keypoints[11], keypoints[12], 0.5f)

        pose.keypoints[2] = keypoints[11]
        pose.keypoints[3] = keypoints[13]
        pose.keypoints[4] = keypoints[15]

        pose.keypoints[5] = keypoints[12]
        pose.keypoints[6] = keypoints[14]
        pose.keypoints[7] = keypoints[16]

        pose.keypoints[8] = keypoints[23]
        pose.keypoints[11] = keypoints[24]

        pose.keypoints[14] = keypoints[2]
        pose.keypoints[15] = keypoints[5]
        pose.keypoints[16] = keypoints[7]
        pose.keypoints[17] = keypoints[8]

        onPosesReceived(mutableListOf(pose))
    }

    override fun handleBadData(event: OSCBadDataEvent?) {
        println("bad osc data received!")
    }

}