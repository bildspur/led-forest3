package ch.bildspur.ledforest

import ch.bildspur.ledforest.artnet.DmxNode
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.Universe
import ch.bildspur.ledforest.ui.PrimaryView
import javafx.stage.Stage
import processing.core.PApplet
import tornadofx.*
import kotlin.concurrent.thread

class LEDForestApp : App() {
    override val primaryView = PrimaryView::class

    lateinit var sketch: Sketch

    init {
    }

    override fun start(stage: Stage) {
        super.start(stage)

        sketch = Sketch()
        sketch.project = createTestConfig()

        thread {
            // run processing app
            PApplet.runSketch(arrayOf("Sketch "), sketch)
        }
    }

    fun createTestConfig(): Project {
        val p = Project()
        p.name = "Test Project"
        p.nodes.add(DmxNode("127.0.0.1", listOf(Universe(0), Universe(1))))
        p.tubes.add(Tube(0, 10, 0))
        return p
    }
}