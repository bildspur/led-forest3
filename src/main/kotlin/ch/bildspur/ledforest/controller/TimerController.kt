package ch.bildspur.ledforest.controller

import ch.bildspur.ledforest.controller.timer.TimerTask
import processing.core.PApplet

/**
 * Created by cansik on 12.07.17.
 */
class TimerController(internal var sketch: PApplet) {

    var taskList = mutableListOf<TimerTask>()

    fun setup() {

    }

    fun update() {
        taskList.forEach {
            val time = sketch.millis()
            if (time - it.lastMillis > it.interval) {
                it.lastMillis = time
                it.block(it)
            }
        }
        taskList = taskList.filter { !it.finished }.toMutableList()
    }

    fun addTask(task: TimerTask) {
        taskList.add(task)
    }
}