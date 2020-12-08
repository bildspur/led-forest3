package ch.bildspur.tracking.simple

import ch.bildspur.math.Float2
import ch.bildspur.math.distance
import ch.bildspur.tracking.Tracker
import java.util.concurrent.atomic.AtomicInteger

class SimpleTracker<TItem>(inline val position: (item: TItem) -> Float2,
                           var maxDelta: Float,
                           var maxUntrackedTime: Long = 100,
                           inline val onAdd: (entity: SimpleTrackedEntity<TItem>) -> Unit = {},
                           inline val onUpdate: (entity: SimpleTrackedEntity<TItem>, item: TItem) -> Unit = { e, i -> e.item = i },
                           inline val onRemove: (entity: SimpleTrackedEntity<TItem>) -> Unit = {},
                           inline val getTime: () -> Long = { System.currentTimeMillis() }) : Tracker<SimpleTrackedEntity<TItem>, TItem> {
    private val trackingIdCounter = AtomicInteger(0)
    override val entities = mutableListOf<SimpleTrackedEntity<TItem>>()

    override fun track(detections: List<TItem>) {
        val millis = getTime()
        val detectedEntities = detections.map { SimplePossibleEntity(it) }
        entities.forEach { it.matched = false }

        detectedEntities.matchNearest(entities,
                maxDelta.toDouble(),
                distance = { s, d -> position(s.item).distance(d.position).toDouble() },
                matched = { it.matched },
                onMatch = { s, d ->
                    d.matched = true
                    s.matched = true
                    d.position = position(s.item)
                    d.lastMatchTimeStamp = millis
                    onUpdate(d, s.item)
                }
        )

        // get untracked entities
        val (expiredEntities, untrackedEntities) = entities.partition { !it.matched && it.getLifeTime(millis) > maxUntrackedTime }

        // predict position of untracked entities
        /*
        untrackedEntities.forEach {
            it.predictNextPosition()
        }
         */

        // clean up entities
        expiredEntities.forEach {
            onRemove(it)
            entities.remove(it)
        }

        // add new entities
        detectedEntities.filter { !it.matched }.forEach {
            val entity = SimpleTrackedEntity(it.item, position(it.item), trackingId = trackingIdCounter.getAndIncrement())
            entities.add(entity)
            onAdd(entity)
        }
    }
}