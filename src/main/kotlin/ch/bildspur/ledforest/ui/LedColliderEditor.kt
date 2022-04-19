package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.model.leda.LedaConfig
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style


class LedColliderEditor(val config: LedaConfig) : Stage() {
    val root = BorderPane()

    init {
        scene = Scene(root, 500.0, 400.0)
        val jMetro = JMetro(Style.DARK)
        jMetro.scene = scene
    }
}