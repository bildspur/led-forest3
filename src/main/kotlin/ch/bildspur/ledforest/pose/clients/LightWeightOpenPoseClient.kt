package ch.bildspur.ledforest.pose.clients

import ch.bildspur.event.Event
import ch.bildspur.ledforest.pose.Pose
import com.illposed.osc.*
import com.illposed.osc.transport.OSCPortIn
import com.illposed.osc.transport.OSCPortInBuilder
import java.net.InetSocketAddress

class LightWeightOpenPoseClient : OSCPacketListener, PoseClient {
    companion object {
        @JvmStatic
        val KEY_POINT_COUNT = 18
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
        if(event == null) return
        val bundle = event.packet as OSCBundle
        val messages = bundle.packets.filterNotNull().map { it as OSCMessage }

        val headerMessage = messages.first()

        if(headerMessage.address != "/poses")
            return

        val poseMessages = messages.drop(1)
        val poses = MutableList(poseMessages.size) { Pose() }
        poseMessages.forEachIndexed { poseIndex, msg ->
            val pose = poses[poseIndex]
            pose.id = -1 // really necessary?
            pose.score = msg.arguments[1] as Float

            for (kp in 0 until KEY_POINT_COUNT) {
                val index = 2 + kp * 2
                pose.keypoints[kp].x = msg.arguments[index] as Float
                pose.keypoints[kp].y = msg.arguments[index + 1] as Float
            }
        }

        //this.poses = poses
        onPosesReceived(poses)
    }

    override fun handleBadData(event: OSCBadDataEvent?) {
        println("bad osc data received!")
    }
}