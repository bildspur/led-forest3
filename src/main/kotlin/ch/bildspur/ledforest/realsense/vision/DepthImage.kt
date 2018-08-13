package ch.bildspur.ledforest.realsense.vision

import org.opencv.core.Mat
import processing.core.PImage

/**
 * Created by cansik on 04.02.17.
 */
class DepthImage(val input: PImage) {
    val components = mutableListOf<ConnectedComponent>()
    lateinit var gray: Mat

    fun release() {
        gray.release()
    }
}