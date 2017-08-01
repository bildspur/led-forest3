package ch.bildspur.floje.controller.timer

/**
 * Created by cansik on 12.07.17.
 */
class TimerTask(val interval: Int, val block: (task: TimerTask) -> Unit) {
    var finished = false
    var lastMillis = 0
}