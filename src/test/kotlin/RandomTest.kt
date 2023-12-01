import ch.bildspur.ledforest.util.ExtendedRandom

fun main() {
    val rnd = ExtendedRandom()
    for (i in 0 until 1000000) {
        val result = rnd.randomInt(0, 3)
        if(result >= 3) {
            throw Exception("Random value generate is higher.")
        }
    }
    println("done!")
}