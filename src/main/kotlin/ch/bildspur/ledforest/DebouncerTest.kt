package ch.bildspur.ledforest

import ch.bildspur.ledforest.util.Debouncer
import ch.bildspur.math.Float2
import ch.bildspur.math.distance
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.stage.Stage


class DebouncerTest : Application() {
    val debouncer = Debouncer(100L, false)

    val windowSize = 512.0
    val radius = 50.0
    val isOneShot = true

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Debouncer Test"

        val circle = Circle(radius)
        circle.centerX = windowSize / 2.0
        circle.centerY = windowSize / 2.0
        val circlePos = Float2(circle.centerX.toFloat(), circle.centerY.toFloat())

        val root = Pane(circle)
        var currentState = false

        root.setOnMouseMoved {
            val mousePos = Float2(it.x.toFloat(), it.y.toFloat())
            val delta = circlePos.distance(mousePos)

            if (delta < radius) {
                if (debouncer.update(true)) {
                    if (currentState != true || !isOneShot) {
                        currentState = true
                        primaryStage.title = "${System.currentTimeMillis()}: mouse is inside!"
                    }
                }
            } else {
                if (!debouncer.update(false)) {
                    if (currentState != false || !isOneShot) {
                        currentState = false
                        primaryStage.title = "${System.currentTimeMillis()}: mouse is now outside"
                    }
                }
            }
        }

        primaryStage.scene = Scene(root, windowSize, windowSize)
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(DebouncerTest::class.java)
        }
    }
}