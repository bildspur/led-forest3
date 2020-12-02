package ch.bildspur.ledforest.pose

import ch.bildspur.event.Event
import com.illposed.osc.*
import com.illposed.osc.transport.udp.OSCPortIn
import com.illposed.osc.transport.udp.OSCPortInBuilder
import java.net.InetSocketAddress
import java.util.concurrent.CopyOnWriteArrayList

class PoseClient(port: Int) : OSCPacketListener {
    companion object {
        @JvmStatic
        val KEY_POINT_COUNT = 18
    }

    val onPosesReceived = Event<MutableList<Pose>>()

    val socketAddress = InetSocketAddress("0.0.0.0", port)

    val server: OSCPortIn = OSCPortInBuilder()
            .setSocketAddress(socketAddress)
            .addPacketListener(this)
            .build()

    init {

        server.startListening()
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