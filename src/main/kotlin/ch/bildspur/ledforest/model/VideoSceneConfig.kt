package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.mapping.Projection2D
import ch.bildspur.ledforest.model.preset.PresetManager
import ch.bildspur.model.DataModel
import ch.bildspur.ui.fx.utils.FileChooserDialogMode
import ch.bildspur.ui.properties.EnumParameter
import ch.bildspur.ui.properties.PathParameter
import com.google.gson.annotations.Expose
import kotlin.io.path.Path

class VideoSceneConfig : PresetManager() {
    @Expose
    @PathParameter("Video", "Select a video", FileChooserDialogMode.Open)
    var videoPath = DataModel(Path(""))

    @Expose
    @EnumParameter("Light Group")
    var lightGroup = DataModel(LightGroup.LED)

    @Expose
    @EnumParameter("UV Projection")
    var projection = DataModel(Projection2D.XY)
}