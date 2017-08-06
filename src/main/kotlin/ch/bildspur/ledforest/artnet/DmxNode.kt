package ch.bildspur.ledforest.artnet

import ch.bildspur.ledforest.model.light.Universe
import ch.bildspur.ledforest.ui.properties.StringParameter
import com.google.gson.annotations.Expose

class DmxNode(@StringParameter("Address") @Expose var address: String, @Expose var universes: MutableList<Universe>) {
    override fun toString(): String {
        return "Node ($address)"
    }
}