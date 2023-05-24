package ch.bildspur.ledforest.scene

import ch.bildspur.color.RGB
import ch.bildspur.event.Event
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.cv.height
import ch.bildspur.ledforest.cv.width
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.LED
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.mapping.Projection2D
import ch.bildspur.ledforest.ui.VideoPreview
import ch.bildspur.ledforest.util.colorizeEach
import ch.bildspur.math.Float2
import ch.bildspur.math.Float3
import ch.bildspur.timer.ElapsedTimer
import ch.bildspur.util.map
import javafx.application.Platform
import org.bytedeco.javacpp.indexer.UByteRawIndexer
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat
import org.bytedeco.opencv.opencv_core.Mat
import processing.core.PVector
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_3BYTE_BGR
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.roundToLong


class VideoScene(project: Project, tubes: List<Tube>) : BaseScene("Video", project, tubes) {

    private val task = TimerTask(1, { update() })
    private var frameGrabber: FFmpegFrameGrabber? = null
    private var videoStartTime = 0L
    private val converterToMat = ToMat()

    private val fpsTimer = ElapsedTimer(33, fireOnStart = true)

    private var mappingFrame = BufferedImage(512, 512, TYPE_3BYTE_BGR)
    private var mappingGraphics: Graphics2D? = null

    override val timerTask: TimerTask
        get() = task

    val onFrame = Event<Mat>()

    init {
        project.videoScene.videoPath.onChanged += {
            restartVideo()
        }

        project.videoScene.fps.onChanged += {
            updateFPS()
        }

        project.videoScene.useVideoFPS.onChanged += {
            updateFPS()
        }

        if (project.videoScene.showDebugPreview.value) {
            Platform.runLater {
                val preview = VideoPreview(this)
                preview.show()
            }
        }
    }

    override fun setup() {
        val videoPath = if (project.videoScene.isAssetPath.value) {
            Paths.get(project.assetDirectory.value.toString(), project.videoScene.videoPath.value.toString())
        } else {
            project.videoScene.videoPath.value
        }

        if (Files.exists(videoPath)) {
            frameGrabber = FFmpegFrameGrabber(videoPath.toString())
            frameGrabber?.start()
            videoStartTime = System.currentTimeMillis()
            updateFPS()
        } else {
            System.err.println("Could not find video at path ${videoPath}.")
        }
    }

    override fun update() {
        if (!fpsTimer.elapsed()) return

        val grabber = frameGrabber ?: return

        // calculate current video time
        val currentTimeStamp = System.currentTimeMillis() - videoStartTime

        val frame = try {
            grabber.setVideoTimestamp(currentTimeStamp * 1000)
            grabber.grab()
        } catch (ex: Exception) {
            println("Video: Could not grab frame!")
            return
        }
        val texture = converterToMat.convertToMat(frame)

        if (texture == null) {
            grabber.setVideoTimestamp(0)
            videoStartTime = System.currentTimeMillis()
            return
        }

        onFrame(texture);

        if (project.videoScene.saveMappingRequested.value)
            initializeMappingFrame(texture)

        val textureIndexer = texture.createIndexer<UByteRawIndexer>()

        val space = PVector.mult(project.interaction.mappingSpace.value, 0.5f)

        tubes.colorizeEach(project.videoScene.lightGroup.value) {
            mapLed(it, textureIndexer, space)
        }

        if (project.videoScene.saveMappingRequested.value) {
            saveMappingFrame()
            project.videoScene.saveMappingRequested.value = false
        }

        textureIndexer.release()
        texture.release()
    }

    private fun mapLed(led: LED, textureIndexer: UByteRawIndexer, space: PVector) {
        val width = textureIndexer.size(1)
        val height = textureIndexer.size(0)

        val normalizedUV = generateUV(led.position, space)

        if (project.videoScene.flipU.value)
            normalizedUV.x = 1.0f - normalizedUV.x

        // always flip v
        if (!project.videoScene.flipV.value)
            normalizedUV.y = 1.0f - normalizedUV.y

        val u = (normalizedUV.x * width).toLong()
        val v = (normalizedUV.y * height).toLong()

        if (project.videoScene.saveMappingRequested.value)
            drawUVMapping(u.toInt(), v.toInt())

        val bgr = IntArray(3)
        textureIndexer.get(v, u, bgr)

        val rgb = RGB(bgr[2], bgr[1], bgr[0])

        if (project.videoScene.fadeLEDs.value) {
            led.color.fade(rgb.toPackedInt(), project.videoScene.fadeSpeed.value)
        } else {
            // set led color
            led.color.set(rgb.toPackedInt())
        }
    }

    private fun generateUV(p: PVector, space: PVector): Float2 {
        val pn = Float3(
            p.x.map(-space.x, space.x, 0f, 1f),
            p.y.map(-space.y, space.y, 0f, 1f),
            p.z.map(-space.z, space.z, 0f, 1f)
        )

        return when (project.videoScene.projection.value) {
            Projection2D.XY -> Float2(pn.x, pn.y)
            Projection2D.XZ -> Float2(pn.x, pn.z)
            Projection2D.YX -> Float2(pn.y, pn.x)
            Projection2D.YZ -> Float2(pn.y, pn.z)
            Projection2D.ZX -> Float2(pn.z, pn.x)
            Projection2D.ZY -> Float2(pn.z, pn.y)
        }
    }

    override fun stop() {
        frameGrabber?.close()
        frameGrabber?.release()
        frameGrabber = null
    }

    override fun dispose() {

    }

    private fun restartVideo() {
        this.stop()
        this.setup()
    }

    private fun updateFPS() {
        // set correct fps
        val videoFPS = frameGrabber?.videoFrameRate
        val fps = if (project.videoScene.useVideoFPS.value && videoFPS != null) {
            videoFPS
        } else {
            project.videoScene.fps.value
        }

        fpsTimer.duration = (1000.0 / max(1.0, fps)).roundToLong()
        fpsTimer.reset()
    }

    private fun initializeMappingFrame(texture: Mat) {
        if (mappingFrame.width != texture.width() || mappingFrame.height != texture.height()) {
            mappingFrame = BufferedImage(texture.width(), texture.height(), TYPE_3BYTE_BGR)
        }

        // create context and clear image
        mappingGraphics = mappingFrame.createGraphics()
        mappingGraphics?.color = java.awt.Color.BLACK
        mappingGraphics?.clearRect(0, 0, mappingFrame.width, mappingFrame.height)
    }

    private fun drawUVMapping(u: Int, v: Int) {
        mappingGraphics?.color = java.awt.Color.GREEN
        mappingGraphics?.drawOval(u, v, 2, 2)
    }

    private fun saveMappingFrame() {
        val outputFile = File("mapping.png")
        ImageIO.write(mappingFrame, "png", outputFile)
        println("Mapping frame has been saved!")
    }
}