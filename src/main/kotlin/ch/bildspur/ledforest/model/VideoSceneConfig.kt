package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.mapping.Projection2D
import ch.bildspur.ledforest.model.preset.PresetManager
import ch.bildspur.ledforest.ui.properties.SeparatorParameter
import ch.bildspur.model.DataModel
import ch.bildspur.ui.fx.utils.FileChooserDialogMode
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import kotlin.io.path.Path

class VideoSceneConfig : PresetManager() {
    @Expose
    @PathParameter("Video", "Select a video", FileChooserDialogMode.Open, extensions = ["*.mp4", "*.mov"])
    var videoPath = DataModel(Path(""))

    @Expose
    @BooleanParameter("Asset Path", useToggleSwitch = true)
    var isAssetPath = DataModel(false)

    @Expose
    @BooleanParameter("Debug Preview", useToggleSwitch = true)
    var showDebugPreview = DataModel(false)

    @Expose
    @BooleanParameter("Use Video FPS", useToggleSwitch = true)
    var useVideoFPS = DataModel(true)

    @Expose
    @NumberParameter("FPS")
    var fps = DataModel(30.0)

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
    @BooleanParameter("Fade LEDs", useToggleSwitch = true)
    var fadeLEDs = DataModel(false)

    @Expose
    @SliderParameter("Fade Speed", 0.001, 1.0, 0.001, snap = true)
    var fadeSpeed = DataModel(0.5f)
}