package ch.bildspur.ledforest.interaction

import com.leapmotion.leap.Controller
import com.leapmotion.leap.Frame
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class LeapDataProvider {
    var isRunning = false
        private set

    private lateinit var leapThread: Thread
    private lateinit var controller: Controller

    val hands = ConcurrentHashMap<Int, InteractionHand>()

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

    private fun readSensor() {
        if (!controller.isConnected)
            return

        if (controller.frame() == null)
            return

        updateHands(controller.frame()!!)
    }

    private fun updateHands(frame: Frame) {
        // add new hands and update known
        frame.hands().forEach {
            // add if not already in cache
            if (!hands.containsKey(it.id()))
                hands.put(it.id(), InteractionHand(it))

            // get hand
            val hand = hands[it.id()]!!
            hand.update()
        }

        // setup alive index
        val handKeys = frame.hands().map { it.id() }.toHashSet()

        // remove old hands
        hands.values.filter { !handKeys.contains(it.hand.id()) }
                .forEach { hands.remove(it.hand.id()) }
    }
}