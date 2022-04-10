package ch.bildspur.ledforest.model.leda

import ch.bildspur.event.Event
import ch.bildspur.ledforest.pose.PoseLandmark
import com.google.gson.annotations.Expose
import processing.core.PVector

abstract class Collider {
    val onCollision = Event<Collision>()
    abstract fun checkCollision(location: PVector, landmark: PoseLandmark) : Boolean
}