package ch.bildspur.ledforest.view

import ch.bildspur.ledforest.artnet.ArtNetClient
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.Universe

class ArtNetRenderer(val artnet: ArtNetClient, val unvierses: List<Universe>, val tubes: List<Tube>) {

    var luminosity = 1f
    var response = 0.5f
    var trace = 0f

    fun render() {
        tubes.groupBy { it.universe }.forEach {
            val universe = unvierses.single { u -> u.id == it.key }
            universe.stageDmx(it.value, luminosity, response, trace)

            artnet.send(universe.id, universe.dmxData)
        }
    }
}