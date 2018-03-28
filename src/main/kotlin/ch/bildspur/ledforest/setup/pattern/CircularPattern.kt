package ch.bildspur.ledforest.setup.pattern

import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.setup.SetupInformation
import processing.core.PApplet

class CircularPattern : BaseClonePattern("Circular") {
    override fun setupPosition(index: Int, tube: Tube, info: SetupInformation) {
        // skip first
        if (index == 0)
            return

        val phi = (1.0 + Math.sqrt(5.0)) / 2.0
        val criclePerRing = 5
        val ring = index / criclePerRing + 1
        val indexOnRing = index % criclePerRing

        // rotate
        val angle = (360.0 / criclePerRing) * indexOnRing
        tube.position.value.rotate(PApplet.radians(angle.toFloat()))

        // translate
        val position = ring * phi * info.space
        if (info.flipXY)
            tube.position.value.add(position.toFloat(), 0f)
        else
            tube.position.value.add(0f, position.toFloat())
    }
}