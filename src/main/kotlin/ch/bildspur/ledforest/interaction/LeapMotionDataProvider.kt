package ch.bildspur.ledforest.interaction

import com.leapmotion.leap.Controller
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

class LeapMotionDataProvider {
    var scan = CopyOnWriteArrayList<InteractionData>()
        internal set

    var isRunning = false
        internal set

    internal lateinit var leapThread: Thread
    internal lateinit var controller: Controller

    fun start() {
        leapThread = thread {
            controller = Controller()

            isRunning = true
            while (isRunning) {
                readSensor()

                // todo: remove sleep, but is needed for performance
                Thread.sleep(100)
            }
        }
    }

    fun stop() {
        isRunning = false
        leapThread.join(5000)
    }

    internal fun readSensor() {
        if (!controller.isConnected)
            return

        if (controller.frame() == null)
            return

        val frame = controller.frame()!!

        println("Hands: ${frame.hands().count()}")
    }
}