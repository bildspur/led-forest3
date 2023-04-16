package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.model.preset.PresetManager
import ch.bildspur.ledforest.model.pulse.PulseSpawnRhythm
import ch.bildspur.ledforest.ui.properties.SeparatorParameter
import ch.bildspur.math.Float3
import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose

class PulseEmitterConfig : PresetManager() {
    @Expose
    @BooleanParameter("Enable Spawn")
    var enableSpawn = DataModel(true)

    @Expose
    @ParameterInformation("How many pulses spawn per time interval.")
    @SliderParameter("Spawn Rate", 1.0, 100.0, 1.0, snap = true, roundInt = true)
    var spawnRate = DataModel(1)

    @Expose
    @ParameterInformation("Timer interval to plan spawns.")
    @SliderParameter("Spawn Interval (s)", 0.0, 10.0, 0.001)
    var spawnInterval = DataModel(1.0f)

    @Expose
    @EnumParameter("Spawn Rhythm")
    var spawnRhythm = DataModel(PulseSpawnRhythm.Random)

    @SeparatorParameter("Pulse")
    private var pulseSep = Any()

    @SeparatorParameter("Location", fontSize = 12.0)
    private var locatinSep = Any()

    @Expose
    @Float3Parameter("Spawn Location")
    var spawnLocation = DataModel(Float3())

    @Expose
    @BooleanParameter("Randomize Location X")
    var randomizeLocationX = DataModel(true)

    @Expose
    @RangeSliderParameter("Location Range X (m)", -10.0, 10.0, 0.001)
    var locationRangeX = DataModel(NumberRange(-4.0, 4.0))

    @Expose
    @BooleanParameter("Randomize Location Y")
    var randomizeLocationY = DataModel(true)

    @Expose
    @RangeSliderParameter("Location Range Y (m)", -10.0, 10.0, 0.001)
    var locationRangeY = DataModel(NumberRange(-4.0, 4.0))

    @Expose
    @BooleanParameter("Randomize Location Z")
    var randomizeLocationZ = DataModel(false)

    @Expose
    @RangeSliderParameter("Location Range Z (m)", -10.0, 10.0, 0.001)
    var locationRangeZ = DataModel(NumberRange(-4.0, 4.0))

    @SeparatorParameter("Color", fontSize = 12.0)
    private var colorSep = Any()

    @Expose
    @RangeSliderParameter("Gradient Spectrum", 0.0, 1.0, 0.01)
    var gradientSpectrum = DataModel(NumberRange(0.0, 0.25))
}