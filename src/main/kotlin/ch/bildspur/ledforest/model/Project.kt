package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Tube
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
    var highResMode = DataModel(true)

    @Expose
    var interaction = Interaction()

    @Expose
    var light = Light()
}