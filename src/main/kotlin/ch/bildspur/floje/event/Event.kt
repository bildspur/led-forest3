package ch.bildspur.floje.event

/**
 * Created by cansik on 09.06.17.
 */
class Event<T> {
    private val handlers = arrayListOf<(Event<T>.(T) -> Unit)>()
    operator fun plusAssign(handler: Event<T>.(T) -> Unit) {
        handlers.add(handler)
    }

    operator fun invoke(value: T) {
        for (handler in handlers) handler(value)
    }
}