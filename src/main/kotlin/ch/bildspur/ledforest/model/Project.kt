package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.interaction.Interaction
import ch.bildspur.ledforest.model.interaction.LeapInteraction
import ch.bildspur.ledforest.model.interaction.RealSenseInteraction
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by cansik on 11.07.17.
 */
class Project {
    @Expose
    @StringParameter("Name")
    var name = DataModel("${Sketch.NAME} Project")

    @Expose
    var nodes = CopyOnWriteArrayList<DmxNode>()

    @Expose
    var tubes = CopyOnWriteArrayList<Tube>()

    @StringParameter("Active Scene", isEditable = false)
    var activeScene = DataModel("-")

    @Expose
    @BooleanParameter("Sound*")
    var isSound = DataModel(true)

    @Expose
    @BooleanParameter("Scene Manager Enabled")
    var isSceneManagerEnabled = DataModel(true)

    @Expose
    @BooleanParameter("ArtNet Rendering")
    var isArtNetRendering = DataModel(true)

    @Expose
    var interaction = Interaction()

    @Expose
    var leapInteraction = LeapInteraction()

    @Expose
    var realSenseInteraction = RealSenseInteraction()

    @Expose
    var visualisation = Visualisation()

    @Expose
    var light = Light()
}