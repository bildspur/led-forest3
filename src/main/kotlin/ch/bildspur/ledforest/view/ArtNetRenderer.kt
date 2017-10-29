package ch.bildspur.ledforest.view

import artnet4j.ArtNetNode
import ch.bildspur.ledforest.artnet.ArtNetClient
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.Universe

class ArtNetRenderer(val project: Project, val artnet: ArtNetClient, val nodes: List<DmxNode>, val tubes: List<Tube>) : IRenderer {
    lateinit var universesToNodes: Map<Universe, ArtNetNode>
    lateinit var indexToUniverses: Map<Int, Universe>

    override val timerTask: TimerTask
        get() = TimerTask(15, { render() })

    override fun setup() {
        buildUniverseIndex()
    }

    override fun render() {
        tubes.groupBy { it.universe.value }.forEach {
            val universe = indexToUniverses[it.key]!!
            val node = universesToNodes[universe]!!

            val light = project.light
            universe.stageDmx(it.value, light.luminosity.value, light.response.value, light.trace.value)
            artnet.send(node, universe.id.value, universe.dmxData)
        }
    }

    fun buildUniverseIndex() {
        universesToNodes = nodes
                .flatMap { n -> n.universes.map { u -> Pair(u, n) } }
                .associate { it.first to artnet.createNode(it.second.address.value)!! }

        indexToUniverses = universesToNodes.keys.associate { it.id.value to it }
    }

    override fun dispose() {

    }
}