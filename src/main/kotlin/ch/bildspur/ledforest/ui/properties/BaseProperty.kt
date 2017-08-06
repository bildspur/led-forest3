package ch.bildspur.ledforest.ui.properties

import javafx.scene.layout.Pane
import java.lang.reflect.Field

abstract class BaseProperty(val field: Field, val obj: Any) : Pane() {
}