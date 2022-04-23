package ch.bildspur.ledforest

import ch.bildspur.ledforest.ui.PrimaryView
import ch.bildspur.ledforest.ui.properties.*
import ch.bildspur.ui.fx.FXPropertyRegistry
import ch.bildspur.ui.properties.PropertiesRegistryEntry
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import kotlin.system.exitProcess


/**
 * Created by cansik on 04.02.17.
 */
class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        // start logger
        BasicConfigurator.configure()
        Logger.getRootLogger().level = Level.ERROR

        // register custom properties
        FXPropertyRegistry.properties.add(PropertiesRegistryEntry(PVectorParameter::class.java, PVectorParameter::name, ::PVectorProperty))
        FXPropertyRegistry.properties.add(PropertiesRegistryEntry(PVectorAngleParameter::class.java, PVectorAngleParameter::name, ::PVectorAngleProperty))
        FXPropertyRegistry.properties.add(PropertiesRegistryEntry(ArrowControlParameter::class.java, ArrowControlParameter::name, ::ArrowControlAction))
        FXPropertyRegistry.properties.add(PropertiesRegistryEntry(SeparatorParameter::class.java, SeparatorParameter::name, ::SeparatorProperty))
        FXPropertyRegistry.properties.add(PropertiesRegistryEntry(CustomUIParameter::class.java, {""}, ::CustomUIElement))

        // start javafx
        val loader = FXMLLoader(javaClass.classLoader.getResource("ch/bildspur/ledforest/ui/PrimaryView.fxml"))
        val root = loader.load<Any>() as Parent
        val controller = loader.getController<Any>() as PrimaryView

        controller.primaryStage = primaryStage

        primaryStage.title = Sketch.NAME
        primaryStage.scene = Scene(root)
        primaryStage.icons.add(Image(javaClass.getResourceAsStream("ui/images/LEDForestIcon.png")))

        val jMetro = JMetro(Style.DARK)
        jMetro.scene = primaryStage.scene

        // setup on shown event
        primaryStage.setOnShown { controller.setupView() }
        primaryStage.isResizable = true

        // style
        root.stylesheets.add("ch/bildspur/ledforest/ui/style/PrimaryView.css")

        primaryStage.setOnCloseRequest {
            if(controller.hasUnsavedChanges.value) {
                when(displaySaveDialog()) {
                    ButtonData.YES -> controller.onSaveProject()
                    ButtonData.CANCEL_CLOSE -> {
                        it.consume()
                        return@setOnCloseRequest
                    }
                    else -> {}
                }
            }

            controller.sketch.stop()
            controller.processingThread.join(5000)
            exitProcess(0)
        }

        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }

    private fun displaySaveDialog(): ButtonData {
        val jMetro = JMetro(Style.DARK)
        val alert = Alert(AlertType.CONFIRMATION)
        jMetro.scene = alert.dialogPane.scene

        alert.title = "Save Project Settings"
        alert.headerText = "There are unsaved project settings."
        alert.contentText = "Would you like to save them?"

        val saveButton = ButtonType("Yes", ButtonData.YES)
        val discardButton = ButtonType("No", ButtonData.NO)
        val cancelButton = ButtonType("Cancel", ButtonData.CANCEL_CLOSE)

        alert.buttonTypes.setAll(saveButton, discardButton, cancelButton)

        val result = alert.showAndWait()
        return result.get().buttonData
    }
}