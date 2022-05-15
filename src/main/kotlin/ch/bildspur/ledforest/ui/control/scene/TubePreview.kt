package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.model.DataModel
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Box
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape3D
import javafx.scene.transform.Translate


class TubePreview(project: DataModel<Project>) : Base3DScene<LED>(project) {
    override fun recreateScene(root: Group) {
        val box = project.value.interaction.interactionBox.value
        val cage = Rectangle(box.x / -2.0, box.y / -2.0, box.x.toDouble(), box.y.toDouble())
        cage.style = "-fx-fill: transparent; -fx-stroke: white; -fx-stroke-width: 0.05;"
        root.children.add(cage)

        project.value.spatialLightElements.forEach { element ->
            element.recalculateLEDPosition()

            element.leds.forEach { led ->
                val width = project.value.visualisation.ledWidth.value.toDouble()
                val ledShape = Box(width * 2, width * 2, element.ledLength.toDouble())

                val pos = led.position.toTranslate()
                ledShape.transforms.add(pos)
                // println(pos)
                /*
                ledShape.transforms.add(Rotate(90.0 + Math.toDegrees(element.rotation.value.x.toDouble()), Rotate.X_AXIS))
                ledShape.transforms.add(Rotate(Math.toDegrees(element.rotation.value.y.toDouble()), Rotate.Y_AXIS))
                ledShape.transforms.add(Rotate(Math.toDegrees(element.rotation.value.z.toDouble()), Rotate.Z_AXIS))
                 */

                val mat = PhongMaterial(Color.WHITE)
                mat.specularPower = 0.0
                ledShape.material = mat

                root.children.add(ledShape)
                shapes[led] = ledShape
            }
        }
    }

    override fun updateShape(element: LED, shape: Shape3D) {
        if (shape.material is PhongMaterial) {
            val mat = (shape.material as PhongMaterial)
            mat.diffuseColor = element.color.toJavaFXColor()
        }

        // maybe update this more efficient
        val translate = shape.transforms.first { it is Translate } as Translate
        translate.x = element.position.x.toDouble()
        translate.y = element.position.y.toDouble()
        translate.z = element.position.z.toDouble()
    }
}