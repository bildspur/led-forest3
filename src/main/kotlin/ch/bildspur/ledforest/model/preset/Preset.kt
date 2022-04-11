package ch.bildspur.ledforest.model.preset

import com.google.gson.annotations.Expose

data class Preset(@Expose val name: String, @Expose var data: String) {
    override fun toString(): String {
        return name
    }
}