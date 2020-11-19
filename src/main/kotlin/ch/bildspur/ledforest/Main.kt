package ch.bildspur.ledforest

import ch.bildspur.ledforest.ui.PrimaryView
import ch.bildspur.ledforest.ui.properties.PVectorAngleParameter
import ch.bildspur.ledforest.ui.properties.PVectorAngleProperty
import ch.bildspur.ledforest.ui.properties.PVectorParameter
import ch.bildspur.ledforest.ui.properties.PVectorProperty
import ch.bildspur.ui.fx.FXPropertyRegistry
import ch.bildspur.ui.fx.properties.ColorProperty
import ch.bildspur.ui.properties.ColorParameter
import ch.bildspur.ui.properties.PropertiesRegistryEntry
import com.sun.javafx.application.PlatformImpl
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger

/**
 * Created by cansik on 04.02.17.
 */
class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        // start logger
        BasicConfigurator.configure()
        Logger.getRootLogger().level = Level.ERROR

        // register properties
        FXPropertyRegistry.properties.add(PropertiesRegistryEntry(PVectorParameter::class.java, PVectorParameter::name, ::PVectorProperty))
        FXPropertyRegistry.properties.add(PropertiesRegistryEntry(PVectorAngleParameter::class.java, PVectorAngleParameter::name, ::PVectorAngleProperty))

        // start javafx
        val loader = FXMLLoader(javaClass.classLoader.getResource("ch/bildspur/ledforest/ui/PrimaryView.fxml"))
        val root = loader.load<Any>() as Parent
        val controller = loader.getController<Any>() as PrimaryView

        controller.primaryStage = primaryStage

        primaryStage.title = Sketch.NAME
        primaryStage.scene = Scene(root)

        // setup on shown event
        primaryStage.setOnShown { controller.setupView() }
        primaryStage.isResizable = false

        // style
        PlatformImpl.setDefaultPlatformUserAgentStylesheet()
        //StyleManager.getInstance().addUserAgentStylesheet(javaClass.classLoader.getResource("ch/bildspur/ledforest/ui/style/ledforest.css").toExternalForm())
        root.stylesheets.add("ch/bildspur/ledforest/ui/style/PrimaryView.css")

        primaryStage.setOnShown { controller.setupView() }
        primaryStage.setOnCloseRequest {
            controller.sketch.stop()
            controller.processingThread.join(5000)
            System.exit(0)
        }

        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}