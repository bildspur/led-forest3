package ch.bildspur.ledforest

import ch.bildspur.ledforest.ui.PrimaryView
import com.sun.javafx.application.PlatformImpl
import com.sun.javafx.css.StyleManager
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Created by cansik on 04.02.17.
 */
class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
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
        StyleManager.getInstance().addUserAgentStylesheet(javaClass.classLoader.getResource("ch/bildspur/ledforest/ui/style/ledforest.css").toExternalForm())

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