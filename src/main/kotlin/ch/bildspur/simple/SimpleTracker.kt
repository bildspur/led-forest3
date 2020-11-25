package ch.bildspur.simple

import ch.bildspur.math.Float2
import ch.bildspur.math.distance
import ch.bildspur.tracking.PossibleEntity
import ch.bildspur.tracking.TrackedEntity
import ch.bildspur.tracking.Tracker
import java.util.concurrent.atomic.AtomicInteger

class SimpleTracker<T>(inline val position: (item: T) -> Float2,
                       inline val onUpdate: (entity : TrackedEntity<T>, item: T) -> Unit = { e, i -> e.item = i },
                       inline val onAdd: (entity : TrackedEntity<T>) -> Unit = {},
                       var maxDelta: Float = 10.0f) : Tracker<T> {
    private val trackingIdCounter = AtomicInteger(0)
    override val entities = mutableListOf<TrackedEntity<T>>()

    override fun track(detections: List<T>) {
        val detectedEntities = detections.map { PossibleEntity(it) }
        entities.forEach { it.matched = false }

        detectedEntities.matchNearest(entities,
            maxDelta.toDouble(),
            distance = { s, d -> position(s.item).distance(d.position).toDouble() },
            matched = { it.matched },
            onMatch = { s, d ->
                d.matched = true
                s.matched = true
                d.position = position(s.item)
                onUpdate(d, s.item)
            }
        )

        // clean up entities
        entities.removeAll { !it.matched }
        entities.forEach { it.lifeTime++ }
        detectedEntities.filter { !it.matched }.forEach {
            val entity = TrackedEntity(it.item, position(it.item), trackingId = trackingIdCounter.getAndIncrement())
            entities.add(entity)
            onAdd(entity)
        }
    }
}