package ch.bildspur.floje

import processing.core.PApplet

/**
 * Created by cansik on 04.02.17.
 */
class Main {
    val sketch = Sketch()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val main = Main()
            main.sketch.args = args
            PApplet.runSketch(arrayOf("Sketch "), main.sketch)
        }
    }
}