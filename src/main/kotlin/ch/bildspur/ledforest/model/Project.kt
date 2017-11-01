package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.ui.properties.BooleanParameter
import ch.bildspur.ledforest.ui.properties.IntParameter
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

    @Expose
    @BooleanParameter("HighRes Mode")
    var highResMode = DataModel(true)

    @Expose
    @BooleanParameter("Fullscreen Mode")
    var isFullScreenMode = DataModel(false)

    @Expose
    @IntParameter("Fullscreen Display")
    var fullScreenDisplay = DataModel(0)

    @Expose
    @BooleanParameter("Sound")
    var isSound = DataModel(true)

    @Expose
    @BooleanParameter("Scene Manager")
    var isSceneManager = DataModel(true)

    @Expose
    @BooleanParameter("Interaction")
    var isInteraction = DataModel(true)

    @Expose
    @BooleanParameter("ArtNet Rendering")
    var isArtNetRendering = DataModel(true)

    @Expose
    var interaction = Interaction()

    @Expose
    var light = Light()
}