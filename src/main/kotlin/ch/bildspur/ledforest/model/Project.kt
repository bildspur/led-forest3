package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.interaction.Interaction
import ch.bildspur.ledforest.model.interaction.LeapInteraction
import ch.bildspur.ledforest.model.interaction.PoseInteraction
import ch.bildspur.ledforest.model.interaction.RealSenseInteraction
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.ActionParameter
import ch.bildspur.ui.properties.BooleanParameter
import ch.bildspur.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by cansik on 11.07.17.
 */
class Project {
    @Expose
    @StringParameter("Name")
    var name = DataModel("${Sketch.NAME} Project")

    @StringParameter("Active Scene", isEditable = false)
    var activeScene = DataModel("-")

    @Expose
    @BooleanParameter("Scene Manager Enabled")
    var isSceneManagerEnabled = DataModel(true)
    
    @ActionParameter("LED Tubes", "Rename")
    var autoNameLEDTubes = {
        tubes.groupBy { it.universe.value }.forEach { u, ts ->
            ts.forEachIndexed { i, t -> t.name.value = "${(65 + u).toChar()}${i + 1}" }
        }
        println("renamed tubes!")
    }

    @Expose
    var nodes = CopyOnWriteArrayList<DmxNode>()

    @Expose
    var tubes = CopyOnWriteArrayList<Tube>()

    @Expose
    var interaction = Interaction()

    @Expose
    var leapInteraction = LeapInteraction()

    @Expose
    var realSenseInteraction = RealSenseInteraction()

    @Expose
    var poseInteraction = PoseInteraction()

    @Expose
    var visualisation = Visualisation()

    @Expose
    var light = Light()

    @Expose
    var map = Map()

    @Expose
    var audio = Audio()
}