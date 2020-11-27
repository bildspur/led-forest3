package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ui.fx.BaseFXFieldProperty
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import java.lang.reflect.Field
import kotlin.concurrent.thread

@Suppress("UNCHECKED_CAST")
class ArrowControlAction(field: Field, obj: Any, val annotation: ArrowControlParameter) : BaseFXFieldProperty(field, obj) {
    val upButton = Button("Up")
    val downButton = Button("Down")
    val leftButton = Button("Left")
    val rightButton = Button("Right")

    val progress = ProgressIndicator()
    val errorText = Label()
    val box = BorderPane(HBox(progress, errorText), upButton, rightButton, downButton, leftButton)

    var buttons = mapOf(upButton to KeyCode.UP,
            downButton to KeyCode.DOWN,
            leftButton to KeyCode.LEFT,
            rightButton to KeyCode.RIGHT)

    init {
        progress.isVisible = false
        progress.maxHeight = 20.0

        val block = field.get(obj) as ((KeyCode) -> Unit)

        errorText.isVisible = false
        errorText.textFill = Color.web("#FF0000")

        buttons.forEach {
            val button = it.key
            val code = it.value

            button.setOnAction {
                progress.isVisible = true
                errorText.isVisible = false
                button.isDisable = true

                val storedCursor = cursor
                cursor = Cursor.WAIT

                thread {
                    try {
                        block(code)
                    } catch (ex: Exception) {
                        errorText.isVisible = true
                        errorText.text = "${ex.message}"
                    } finally {
                        Platform.runLater {
                            cursor = storedCursor
                            button.isDisable = false
                            progress.isVisible = false
                            if (annotation.invokesChange)
                                propertyChanged.invoke(this)
                        }
                    }
                }
            }
        }

        //box.spacing = 10.0
        children.add(box)
    }
}