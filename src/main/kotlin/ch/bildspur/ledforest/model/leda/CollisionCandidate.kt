package ch.bildspur.ledforest.model.leda

import ch.bildspur.ledforest.pose.PoseLandmark
import processing.core.PVector

data class CollisionCandidate(val location: PVector, val landmark: PoseLandmark)