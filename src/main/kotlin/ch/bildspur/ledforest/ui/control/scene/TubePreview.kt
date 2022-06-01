package ch.bildspur.ledforest.ui.control.scene

import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.ui.control.scene.shapes.WireBox
import ch.bildspur.model.DataModel
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.*
import javafx.scene.transform.Translate


class TubePreview(project: DataModel<Project>) : Base3DScene<Any>(project) {
    private val reactorShapes = mutableListOf<Shape3D>()

    override fun recreateScene(root: Group) {
        val box = project.value.interaction.mappingSpace.value
        root.children.add(WireBox(box.x.toDouble(), box.y.toDouble(), box.z.toDouble()))

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

        // add reactor shapes
        reactorShapes.clear()
        for (i in 0 until 4) {
            val sphere = Sphere(0.3)
            sphere.drawMode = DrawMode.LINE
            sphere.transforms.add(Translate())
            root.children.add(sphere)
            reactorShapes.add(sphere)
        }
    }

    override fun updateShape(element: Any, shape: Shape3D) {
        when (element) {
            is LED -> {
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
    }

    override fun postRender() {
        super.postRender()

        // check for poses
        reactorShapes.forEach { it.isVisible = false }

        if (!project.value.poseInteraction.showReactors.value) return
        val reactors = project.value.poseInteraction.activeReactors
        reactors.forEachIndexed { index, reactor ->
            val shape = reactorShapes[index]

            val translate = shape.transforms.first { it is Translate } as Translate
            translate.x = reactor.position.x.toDouble()
            translate.y = reactor.position.y.toDouble()
            translate.z = reactor.position.z.toDouble()

            shape.isVisible = true
        }
    }
}