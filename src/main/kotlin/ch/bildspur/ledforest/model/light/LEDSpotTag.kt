package ch.bildspur.ledforest.model.light

enum class LEDSpotTag(val label: String) {
    None("None"),
    General("General"),
    Accent("Accent");

    override fun toString(): String {
        return label
    }
}