package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.event.Event


/**
 * Created by cansik on 09.06.17.
 */
class DataModel<T>(@Volatile private var dataValue: T) {
    val onChanged = Event<T>()

    var value: T
        get() = this.dataValue
        set(value) {
            var oldValue = dataValue
            dataValue = value

            // fire event if changed
            if (dataValue != oldValue)
                fire()
        }

    fun fire() {
        onChanged.invoke(dataValue)
    }
}