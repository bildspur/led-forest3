package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.preset.PresetManager
import ch.bildspur.model.DataModel
import ch.bildspur.ui.fx.utils.FileChooserDialogMode
import ch.bildspur.ui.properties.PathParameter
import com.google.gson.annotations.Expose

class VideoSceneConfig : PresetManager() {
    @Expose
    @PathParameter("Video", "Select a video", FileChooserDialogMode.Open, extensions = ["*.mov", "*.mp4"])
    var videoPath = DataModel("")
}