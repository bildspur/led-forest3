package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.util.ColorMixer
import ch.bildspur.ledforest.util.distance
import ch.bildspur.ledforest.util.toFloat3
import ch.bildspur.math.Float3

class GraphScene(project: Project, tubes: List<Tube>) : BaseScene("Graph", project, tubes) {

    private val task = TimerTask(10, { update() })

    override val timerTask: TimerTask
    get() = task

    data class Node(val position: Float3, val tubes: MutableList<Tube> = mutableListOf())

    private val colorMixer = ColorMixer()
    private val graph = mutableListOf<Node>()

    override fun setup() {
        createGraph()
    }

    override fun update() {

    }

    override fun stop() {

    }

    override fun dispose() {

    }

    private fun createGraph() {
        // reset graph
        graph.clear()

        for(tube in tubes) {
            listOf(tube.leds.first(), tube.leds.last()).forEach { p ->
                val closeNodes = graph.filter { it.position.distance(p.position.toFloat3()) < project.graphScene.maxNodeDistance.value }

                if (closeNodes.isEmpty()) {
                    graph.add(Node(p.position.toFloat3(), mutableListOf(tube)))
                } else {
                    closeNodes[0].tubes.add(tube)
                }
            }
        }
    }
}