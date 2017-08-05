package ch.bildspur.ledforest

import ch.bildspur.ledforest.ui.PrimaryView
import javafx.stage.Stage
import tornadofx.*

class LEDForestApp : App() {
    override val primaryView = PrimaryView::class

    init {
    }

    override fun start(stage: Stage) {
        super.start(stage)
    }
}