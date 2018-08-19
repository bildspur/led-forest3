package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.stackMatrix
import ch.bildspur.ledforest.util.translate
import processing.core.PApplet
import processing.core.PVector

abstract class BaseInteractionScene(name: String, project: Project, tubes: List<Tube>) : BaseScene(name, project, tubes) {
    var space = Sketch.instance.createGraphics(10, 10, PApplet.P3D)

    fun getLEDPosition(index: Int, tube: Tube): PVector {
        val position = PVector()
        space.stackMatrix {
            // translate normalizedPosition
            it.translate(tube.position.value)

            // global rotation
            it.rotateX(tube.rotation.value.x)
            it.rotateY(tube.rotation.value.y)
            it.rotateZ(tube.rotation.value.z)

            // translate height
            it.translate(0f, 0f, (if (tube.inverted.value) -1 else 1) * (index * project.visualisation.ledHeight.value))

            // rotate shape
            it.rotateX(PApplet.radians(90f))

            position.x = it.modelX(0f, 0f, 0f)
            position.y = it.modelY(0f, 0f, 0f)
            position.z = it.modelZ(0f, 0f, 0f)
        }

        return position
    }
}