package ch.bildspur.ledforest.pose.clients

import ch.bildspur.event.Event
import ch.bildspur.ledforest.pose.Pose

interface PoseClient {
    fun start(port: Int)
    val onPosesReceived : Event<MutableList<Pose>>
    fun close()
}