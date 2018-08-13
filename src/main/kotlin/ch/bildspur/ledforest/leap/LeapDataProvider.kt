package ch.bildspur.ledforest.leap

import ch.bildspur.ledforest.controller.timer.Timer
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.DataModel
import ch.bildspur.ledforest.model.Project
import com.leapmotion.leap.Controller
import com.leapmotion.leap.Frame
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread

class LeapDataProvider(val project: DataModel<Project>) {
    private val updateTime = 15L

    var isRunning = false
        private set

    var handCount = 0

    private lateinit var leapThread: Thread
    private lateinit var controller: Controller

    private val timer = Timer()

    private val handCache = ConcurrentHashMap<Int, InteractionHand>()

    /**
     * Not thread safe -> Maybe using entry to make it concurrency safe
     */
    val hands: MutableCollection<InteractionHand>
        get() = handCache.values //entries.map { it -> it.value }.toMutableList()

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

        if (::leapThread.isInitialized)
            leapThread.join(5000)
    }

    private fun readSensor() {
        if (!controller.isConnected)
            return

        if (controller.frame() == null)
            return

        if (!project.value.interaction.isInteractionOn.value)
            return

        updateHands(controller.frame()!!)
    }

    private fun updateHands(frame: Frame) {
        // add new hand and update known
        frame.hands().forEach {
            // add if not already in cache
            if (!handCache.containsKey(it.id())) {
                handCache[it.id()] = InteractionHand(it, project.value.interaction.interactionBox.value)
                handCount++
            }

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