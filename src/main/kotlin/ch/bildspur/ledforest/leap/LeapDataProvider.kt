package ch.bildspur.ledforest.leap

import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import com.leapmotion.leap.Controller
import com.leapmotion.leap.Frame
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class LeapDataProvider {
    private val updateTime = 15L

    var isRunning = false
        private set

    private lateinit var leapThread: Thread
    private lateinit var controller: Controller

    private val timer = Timer()

    private val handCache = ConcurrentHashMap<Int, InteractionHand>()

    val hands: MutableCollection<InteractionHand>
        get() = handCache.values

    init {
        timer.addTask(TimerTask(updateTime, {
            readSensor()
        }))
    }

    fun start() {
        leapThread = thread {
            controller = Controller()

            isRunning = true
            while (isRunning) {
                timer.update()
                Thread.sleep(updateTime)
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
            if (!handCache.containsKey(it.id()))
                handCache.put(it.id(), InteractionHand(it))

            // get hand
            val hand = handCache[it.id()]!!
            hand.hand = it
            hand.update()
        }

        // setup alive index
        val handKeys = frame.hands().map { it.id() }.toHashSet()

        // remove old hands
        handCache.values.filter { !handKeys.contains(it.hand.id()) }
                .forEach { handCache.remove(it.hand.id()) }
    }
}