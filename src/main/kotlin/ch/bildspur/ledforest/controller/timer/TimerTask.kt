package ch.bildspur.ledforest.controller.timer

/**
 * Created by cansik on 12.07.17.
 */
class TimerTask(var interval: Long, val block: (task: TimerTask) -> Unit, val name: String = "") {
    var finished = false
    var lastMillis = 0L
}