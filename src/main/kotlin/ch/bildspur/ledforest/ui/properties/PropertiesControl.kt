package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ledforest.event.Event
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.lang.reflect.Field

class PropertiesControl : VBox() {

    val propertyChanged = Event<BaseProperty>()

    fun initView(obj: Any) {
        clearView()

        val params = readParameters(obj)

        // create view
        params.forEach {
            if (it.isAnnotationPresent(StringParameter::class.java)) {
                val annotation = it.getAnnotation(StringParameter::class.java)
                addProperty(annotation.name, StringProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(NumberParameter::class.java)) {
                val annotation = it.getAnnotation(NumberParameter::class.java)
                addProperty(annotation.name, NumberProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(SliderParameter::class.java)) {
                val annotation = it.getAnnotation(SliderParameter::class.java)
                addProperty(annotation.name, SliderProperty(it, obj, annotation))
            }

            if (it.isAnnotationPresent(BooleanParameter::class.java)) {
                val annotation = it.getAnnotation(BooleanParameter::class.java)
                addProperty(annotation.name, BooleanProperty(it, obj, annotation))
            }
        }
    }

    fun clearView() {
        this.children.clear()
    }

    private fun addProperty(name: String, propertyView: BaseProperty) {
        propertyView.propertyChanged += {
            propertyChanged(propertyView)
        }

        val nameLabel = Label("$name:")
        nameLabel.prefWidth = 80.0

        val box = HBox(nameLabel, propertyView)
        box.spacing = 10.0
        box.prefHeight = 20.0
        children.add(box)
    }

    private fun readParameters(obj: Any): List<Field> {
        val c = obj.javaClass

        val fields = c.declaredFields.filter {
            it.isAnnotationPresent(SliderParameter::class.java) ||
                    it.isAnnotationPresent(NumberParameter::class.java) ||
                    it.isAnnotationPresent(StringParameter::class.java) ||
                    it.isAnnotationPresent(BooleanParameter::class.java)
        }
        fields.forEach { it.isAccessible = true }
        return fields
    }
}