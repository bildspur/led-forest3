package ch.bildspur.ledforest.ui

import ch.bildspur.ledforest.cv.*
import ch.bildspur.ledforest.scene.VideoScene
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import org.bytedeco.opencv.global.opencv_core.CV_8UC1
import org.bytedeco.opencv.global.opencv_imgproc.COLOR_GRAY2BGR
import org.bytedeco.opencv.opencv_core.Mat
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_3BYTE_BGR
import kotlin.io.path.name

class VideoPreview(val videoScene: VideoScene) : Stage() {
    private val root = BorderPane()
    private val canvas = Canvas(512.0, 512.0);

    init {
        title = "Video Preview: ${videoScene.project.videoScene.videoPath.value.name}"

        scene = Scene(root, 512.0, 512.0)
        this.scene = scene

        canvas.cursor = Cursor.CROSSHAIR
        val g = canvas.graphicsContext2D
        g.fill = Color.BLACK

        root.center = canvas

        val jMetro = JMetro(Style.DARK)
        jMetro.scene = scene

        var inputFrame = BufferedImage(512, 512, TYPE_3BYTE_BGR)

        // add on frame event
        videoScene.onFrame += {
            if (inputFrame.width != it.width() || inputFrame.height != it.height()) {
                inputFrame = BufferedImage(it.width(), it.height(), TYPE_3BYTE_BGR)
            }

            createBufferedImage(it, inputFrame)
            val image = SwingFXUtils.toFXImage(inputFrame, null)

            Platform.runLater {
                // clear canvas
                g.fill = Color.BLACK
                g.fillRect(0.0, 0.0, canvas.width, canvas.height)

                if (image != null)
                    g.drawImage(image, 0.0, 0.0, canvas.width, canvas.height)
            }
        }
    }

    private fun createBufferedImage(mat: Mat, image: BufferedImage): BufferedImage {
        if (mat.type() == CV_8UC1)
            mat.convertColor(COLOR_GRAY2BGR)

        return mat.toFrame().createBufferedImageFast(image)
    }

    override fun close() {
        super.close()
        videoScene.onFrame.clear()
    }
}