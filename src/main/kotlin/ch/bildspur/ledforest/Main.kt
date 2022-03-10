package ch.bildspur.ledforest

import ch.bildspur.ledforest.ui.PrimaryView
import ch.bildspur.ledforest.ui.properties.*
import ch.bildspur.ui.fx.FXPropertyRegistry
import ch.bildspur.ui.properties.PropertiesRegistryEntry
import com.sun.javafx.application.PlatformImpl
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
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
        FXPropertyRegistry.properties.add(PropertiesRegistryEntry(ArrowControlParameter::class.java, ArrowControlParameter::name, ::ArrowControlAction))

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
        primaryStage.isResizable = false

        // style
        PlatformImpl.setDefaultPlatformUserAgentStylesheet()
        //StyleManager.getInstance().addUserAgentStylesheet(javaClass.classLoader.getResource("ch/bildspur/ledforest/ui/style/ledforest.css").toExternalForm())
        root.stylesheets.add("ch/bildspur/ledforest/ui/style/PrimaryView.css")

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