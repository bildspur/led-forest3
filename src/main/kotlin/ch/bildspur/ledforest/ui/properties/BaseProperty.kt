package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.event.Event
import javafx.scene.layout.Pane
import java.lang.reflect.Field

abstract class BaseProperty(val field: Field, val obj: Any) : Pane() {
    val propertyChanged = Event<BaseProperty>()
}