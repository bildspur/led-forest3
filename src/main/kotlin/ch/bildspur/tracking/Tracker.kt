package ch.bildspur.tracking

interface Tracker<T> {
    val entities : List<TrackedEntity<T>>
    fun track(detections : List<T>)
}