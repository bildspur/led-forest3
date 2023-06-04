package ch.bildspur.ledforest.model

import ch.bildspur.event.Event
import ch.bildspur.ledforest.model.mapping.Projection2D
import ch.bildspur.ledforest.model.preset.PresetManager
import ch.bildspur.ledforest.scene.VideoScene
import ch.bildspur.ledforest.ui.VideoPreview
import ch.bildspur.ledforest.ui.properties.SeparatorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.fx.utils.FileChooserDialogMode
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import javafx.application.Platform
import org.bytedeco.opencv.opencv_core.Mat
import kotlin.io.path.Path

class VideoSceneConfig : PresetManager() {
    @Expose
    @PathParameter("Video", "Select a video", FileChooserDialogMode.Open, extensions = ["*.mp4", "*.mov"])
    var videoPath = DataModel(Path(""))

    @Expose
    @BooleanParameter("Asset Path", useToggleSwitch = true)
    var isAssetPath = DataModel(false)

    @Expose
    @BooleanParameter("Use Video FPS", useToggleSwitch = true)
    var useVideoFPS = DataModel(true)

    @Expose
    @NumberParameter("FPS")
    var fps = DataModel(30.0)

    @SeparatorParameter("Debug")
    private val debugSep = Any()

    @ActionParameter("Preview", "Open")
    private var displayPreview = {
        Platform.runLater {
            val preview = VideoPreview(this)
            preview.show()
        }
    }

    @BooleanParameter("Request Mapping", useToggleSwitch = true)
    var saveMappingRequested = DataModel(false)

    @SeparatorParameter("Mapping")
    private val mappingSep = Any()

    @Expose
    @EnumParameter("Light Group")
    var lightGroup = DataModel(LightGroup.LED)

    @Expose
    @EnumParameter("UV Projection")
    var projection = DataModel(Projection2D.XY)

    @Expose
    @BooleanParameter("Flip U", useToggleSwitch = true)
    var flipU = DataModel(false)

    @Expose
    @BooleanParameter("Flip V", useToggleSwitch = true)
    var flipV = DataModel(false)

    @Expose
    @BooleanParameter("Swap UV", useToggleSwitch = true)
    var swapUV = DataModel(false)

    @Expose
    @BooleanParameter("Fade LEDs", useToggleSwitch = true)
    var fadeLEDs = DataModel(false)

    @Expose
    @SliderParameter("Fade Speed", 0.001, 1.0, 0.001, snap = true)
    var fadeSpeed = DataModel(0.5f)

    val videoStartTime = DataModel(0L)

    val onFrame = Event<Mat>()
    val onVideoEnded = Event<VideoScene>()
}