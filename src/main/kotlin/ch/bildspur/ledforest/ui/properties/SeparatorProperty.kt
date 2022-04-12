package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ui.fx.BaseFXFieldProperty
import ch.bildspur.ui.properties.PropertyComponent
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import java.lang.reflect.Field

class SeparatorProperty(field: Field, obj: Any, annotation: SeparatorParameter) : BaseFXFieldProperty(field, obj), PropertyComponent {

    init {
        alignment = Pos.CENTER
        padding = Insets( annotation.topPadding, 0.0,  annotation.bottomPadding, 0.0)

        val first = Separator()
        setHgrow(first, Priority.ALWAYS)
        children.add(first)

        if(annotation.name.isNotEmpty()) {
            val label = Label(annotation.name)
            label.font = Font.font("Helvetica", FontWeight.BOLD, annotation.fontSize)
            children.add(label)

            val second = Separator()
            setHgrow(second, Priority.ALWAYS)
            children.add(second)
        }
    }
}