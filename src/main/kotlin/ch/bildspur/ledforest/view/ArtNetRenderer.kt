package ch.bildspur.ledforest.view

import ch.bildspur.artnet.ArtNetNode
import ch.bildspur.ledforest.artnet.ArtNetClient
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Universe

class ArtNetRenderer(val project: Project, val artnet: ArtNetClient, val nodes: List<DmxNode>) : IRenderer {
    lateinit var universesToNodes: Map<Universe, ArtNetNode>
    lateinit var indexToUniverses: Map<Int, Universe>

    private val task = TimerTask(15, { render() }, "ArtNetRenderer")
    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        buildUniverseIndex()
    }

    override fun render() {
        // check if artnet rendering is used
        if (!project.light.isArtNetRendering.value) return

        val elements = project.lightElements

        val light = project.light
        val maxBrightness = light.globalBrightness.value * light.luminosity.value

        elements.groupBy { it.universe.value }.forEach {
            val universe = indexToUniverses[it.key] ?: return@forEach
            val node = universesToNodes[universe] ?: return@forEach

            val universeMaxBrightness = if(universe.overwriteGlobalBrightness.value) {
                light.luminosity.value * universe.brightness.value
            } else {
                maxBrightness * universe.brightness.value
            }

            universe.stageDmx(
                it.value,
                universeMaxBrightness,
                light.response.value,
                light.trace.value,
                light.brightnessCutoff.value,
                light.brightnessCurve.value
            )
            artnet.send(node, universe.id.value, universe.dmxData)
        }
    }

    fun buildUniverseIndex() {
        universesToNodes = nodes.flatMap { n -> n.universes.map { u -> Pair(u, n) } }
            .associate { it.first to artnet.createNode(it.second.address.value)!! }

        indexToUniverses = universesToNodes.keys.associateBy { it.id.value }
    }

    override fun dispose() {

    }
}