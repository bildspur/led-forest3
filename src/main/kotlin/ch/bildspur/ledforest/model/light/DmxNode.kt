package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import java.util.concurrent.CopyOnWriteArrayList

class DmxNode(@StringParameter("Address") @Expose var address: String, @Expose var universes: CopyOnWriteArrayList<Universe>) {
    override fun toString(): String {
        return "Node ($address)"
    }
}