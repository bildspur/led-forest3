package ch.bildspur.tracking

interface Tracker<TEntity : TrackedEntity<TItem>, TItem> {
    val entities : List<TEntity>
    fun track(detections : List<TItem>)
}