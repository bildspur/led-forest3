package ch.bildspur.ledforest.ui.properties

import ch.bildspur.ui.fx.BaseFXFieldProperty
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
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
    private val upButton = Button("\u2191")
    private val downButton = Button("\u2193")
    private val leftButton = Button("\u2190")
    private val rightButton = Button("\u2192")

    private val progress = ProgressIndicator()
    private val errorText = Label()
    private val errorBox = HBox(progress, errorText)
    private val box = BorderPane(errorBox, upButton, rightButton, downButton, leftButton)

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

        BorderPane.setAlignment(errorBox, Pos.CENTER)
        BorderPane.setMargin(errorBox, Insets(5.0))
        errorBox.alignment = Pos.CENTER

        buttons.forEach {
            val button = it.key
            val code = it.value

            // center
            BorderPane.setAlignment(button, Pos.CENTER)
            BorderPane.setMargin(button, Insets(5.0))

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

        children.add(box)
    }
}