package ch.bildspur.ledforest.model.light

enum class TubeTag(name: String) {
    None("None"),
    Interaction("Interaction"),
    CubeTop("Cube Top"),
    CubeBottom("Cube Bottom");

    override fun toString(): String {
        return name
    }
}