package ch.bildspur.ledforest.model.light

import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.ui.properties.StringParameter
import com.google.gson.annotations.Expose
import java.util.concurrent.CopyOnWriteArrayList

class DmxNode(@StringParameter("Address") @Expose var address: DataModel<String> = DataModel("127.0.0.1"),
              @Expose var universes: CopyOnWriteArrayList<Universe> = CopyOnWriteArrayList()) {
    override fun toString(): String {
        return "Node (${address.value})"
    }
}