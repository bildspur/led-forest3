package ch.bildspur.ledforest.view

import ch.bildspur.artnet.ArtNetNode
import ch.bildspur.ledforest.artnet.ArtNetClient
import ch.bildspur.ledforest.artnet.recorder.ArtNetSample
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.Universe
import ch.bildspur.timer.ElapsedTimer

class ArtNetRenderer(val project: Project, val artnet: ArtNetClient, val nodes: List<DmxNode>) : IRenderer {
    lateinit var universesToNodes: Map<Universe, ArtNetNode>
    lateinit var indexToUniverses: Map<Int, Universe>

    private val recordingTimer = ElapsedTimer(33)

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

        // recording
        recordingTimer.duration = project.light.recorder.sampleTime.value.toLong()
        var record = project.light.recorder.isRecording.value
        if (!recordingTimer.elapsed())
            record = false

        elements.groupBy { it.universe.value }.forEach {
            val universe = indexToUniverses[it.key] ?: return@forEach
            val node = universesToNodes[universe] ?: return@forEach

            val universeMaxBrightness = if (universe.overwriteGlobalBrightness.value) {
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

            if (record) {
                recordArtNet(universe.id.value, universe.dmxData)
            }
        }
    }

    private fun recordArtNet(universe: Int, data: ByteArray) {
        val sample = ArtNetSample(System.currentTimeMillis(), 0, universe, 0, data)
        project.light.recorder.addSample(sample)
    }

    fun buildUniverseIndex() {
        universesToNodes = nodes.flatMap { n -> n.universes.map { u -> Pair(u, n) } }
            .associate { it.first to artnet.createNode(it.second.address.value)!! }

        indexToUniverses = universesToNodes.keys.associateBy { it.id.value }
    }

    override fun dispose() {

    }
}