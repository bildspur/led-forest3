package ch.bildspur.ledforest.artnet

import ch.bildspur.ledforest.model.light.Universe
import com.google.gson.annotations.Expose

class DmxNode(@Expose var address: String, @Expose var universes: List<Universe>)