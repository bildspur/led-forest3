package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.artnet.recorder.ArtNetBuffer
import ch.bildspur.ledforest.artnet.recorder.ArtNetSample
import ch.bildspur.model.DataModel
import ch.bildspur.ui.fx.utils.FileChooserDialogMode
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import javafx.application.Platform
import kotlin.io.path.Path

class ArtNetRecorder {
    private val buffer = ArtNetBuffer("Created with LED Forest")

    @StringParameter("Samples", isEditable = false)
    var sampleCount = DataModel("0")

    @BooleanParameter("Recording")
    var isRecording = DataModel(false)

    @Expose
    @NumberParameter("Sample Time", unit = "ms")
    var sampleTime = DataModel(33)

    @PathParameter("Output File", mode = FileChooserDialogMode.Save, extensions= arrayOf("*.artnet"))
    var outputPath = DataModel(Path("recording.artnet"))

    @ActionParameter("Buffer", "Save")
    private val save = {
        val data = buffer.write(compressed = true)
        outputPath.value.toFile().writeBytes(data.array())
    }

    @ActionParameter("Buffer", "Reset")
    private val reset = {
        buffer.clear()
        updateSampleSize()
    }

    private fun updateSampleSize() {
        Platform.runLater {
            sampleCount.value = "${buffer.samples.size}"
        }
    }

    fun addSample(sample: ArtNetSample) {
        buffer.samples.add(sample)
        updateSampleSize()
    }
}