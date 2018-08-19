package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.event.Event
import com.google.gson.annotations.Expose


/**
 * Created by cansik on 09.06.17.
 */
class DataModel<T>(@Expose @Volatile private var dataValue: T) {
    val onChanged = Event<T>()
    private var publishActive = true

    var value: T
        get() = this.dataValue
        set(value) {
            val oldValue = dataValue
            dataValue = value

            // fire event if changed
            if (publishActive && dataValue != oldValue)
                fire()
        }

    fun setSilent(value: T) {
        publishActive = false
        this.value = value
        publishActive = true
    }

    fun fire() {
        onChanged(dataValue)
    }

    fun fireLatest() {
        onChanged.invokeLatest(dataValue)
    }

    override fun toString(): String {
        return "DataModel ($value)"
    }
}