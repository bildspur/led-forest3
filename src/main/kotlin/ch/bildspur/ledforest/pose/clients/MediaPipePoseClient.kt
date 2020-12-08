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

        for (i in 0 until KEY_POINT_COUNT) {
            var index = 1 + i * 4

            keypoints[i].x = message.arguments[index++] as Float
            keypoints[i].y = message.arguments[index++] as Float
            keypoints[i].z = message.arguments[index] as Float

            //pose.keypoints.get(i).visibility = msg.get(index++).floatValue()
        }

        // ugly mapping to full body pose system


        onPosesReceived(mutableListOf(pose))
    }

    override fun handleBadData(event: OSCBadDataEvent?) {
        println("bad osc data received!")
    }

}