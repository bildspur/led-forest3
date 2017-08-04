package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.artnet.DmxNode
import ch.bildspur.ledforest.model.light.Tube
import com.google.gson.annotations.Expose

/**
 * Created by cansik on 11.07.17.
 */
class Project {
    @Expose var name = "${Sketch.NAME} Project"

    @Expose var nodes = mutableListOf<DmxNode>()

    @Expose var tubes = mutableListOf<Tube>()
}