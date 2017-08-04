package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.Sketch
import javafx.scene.layout.BorderPane
import tornadofx.*

class PrimaryView : View(Sketch.NAME) {
    override val root: BorderPane by fxml()

    init {

    }
}
