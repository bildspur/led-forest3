package ch.bildspur.ledforest.ui.properties

import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import java.lang.reflect.Field

class PropertiesControl : VBox() {

    fun initView(obj: Any) {
        clearView()

        val params = readParameters(obj)

        // create view
        params.forEach {
            if (it.isAnnotationPresent(StringParameter::class.java)) {
                val annotation = it.getAnnotation(StringParameter::class.java)
                addProperty(annotation.name, StringProperty(it, obj, annotation))
            }
        }
    }

    fun clearView() {
        this.children.clear()
    }

    private fun addProperty(name: String, propertyView: BaseProperty) {
        val nameLabel = Label("$name:")
        nameLabel.prefWidth = 80.0

        val box = HBox(nameLabel, propertyView)
        box.spacing = 10.0
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