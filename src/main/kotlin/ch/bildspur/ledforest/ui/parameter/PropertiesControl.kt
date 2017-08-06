package ch.bildspur.ledforest.ui.parameter

import javafx.scene.layout.VBox
import java.lang.reflect.Field

class PropertiesControl : VBox() {

    fun initView(obj: Any) {
        println("Init view: $obj")
    }

    private fun readParameters(obj: Any): List<Field> {
        val c = obj.javaClass

        val fields = c.declaredFields.filter {
            it.isAnnotationPresent(SliderParameter::class.java) ||
                    it.isAnnotationPresent(NumberParameter::class.java) ||
                    it.isAnnotationPresent(TextParameter::class.java)
        }
        fields.forEach { it.isAccessible = true }
        return fields
    }
}