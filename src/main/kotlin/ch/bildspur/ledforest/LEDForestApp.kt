package ch.bildspur.ledforest

import ch.bildspur.ledforest.ui.PrimaryView
import javafx.stage.Stage
import processing.core.PApplet
import tornadofx.*
import kotlin.concurrent.thread

class LEDForestApp : App() {
    override val primaryView = PrimaryView::class

    var sketch = Sketch()

    init {
    }

    override fun start(stage: Stage) {
        super.start(stage)

        thread {
            // run processing app
            PApplet.runSketch(arrayOf("Sketch "), sketch)
        }
    }
}