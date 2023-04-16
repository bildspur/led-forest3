package ch.bildspur.ledforest.model

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.ledforest.model.preset.PresetManager
import ch.bildspur.ledforest.model.pulse.PulseSpawnRhythm
import ch.bildspur.ledforest.ui.properties.SeparatorParameter
import ch.bildspur.math.Float3
import ch.bildspur.model.DataModel
import ch.bildspur.model.NumberRange
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose

class PulseEmitterConfig : PresetManager() {
    @ActionParameter("Pulses", "Clear")
    private var clearPulses = {
        Sketch.instance.project.value.pulseScene.pulses.clear()
    }

    @SeparatorParameter("Spawn")
    private var pulseSep = Any()

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

    // location
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

    // time
    @SeparatorParameter("Time", fontSize = 12.0)
    private var timeSep = Any()

    @Expose
    @SliderParameter("Pulse Duration (s)", 0.0, 20.0, 0.001)
    var pulseDuration = DataModel(1.0f)

    @Expose
    @BooleanParameter("Randomize Pulse Duration")
    var randomizePulseDuration = DataModel(true)

    @Expose
    @RangeSliderParameter("Pulse Duration Range (s)", 0.0, 20.0, 0.001)
    var pulseDurationRange = DataModel(NumberRange(4.0, 8.0))

    // delay
    @SeparatorParameter("Delay", fontSize = 12.0)
    private var delaySep = Any()

    @Expose
    @SliderParameter("Pulse Delay (s)", 0.0, 20.0, 0.001)
    var pulseDelay = DataModel(0.0f)

    @Expose
    @BooleanParameter("Randomize Pulse Delay")
    var randomizePulseDelay = DataModel(false)

    @Expose
    @RangeSliderParameter("Pulse Delay Range (s)", 0.0, 5.0, 0.001)
    var pulseDelayRange = DataModel(NumberRange(0.0, 1.0))

    // distance
    @SeparatorParameter("Distance", fontSize = 12.0)
    private var distanceSep = Any()

    @Expose
    @SliderParameter("Pulse Distance (m)", 0.0, 20.0, 0.001)
    var pulseDistance = DataModel(10.0f)

    @Expose
    @BooleanParameter("Randomize Pulse Distance")
    var randomizePulseDistance = DataModel(false)

    @Expose
    @RangeSliderParameter("Pulse Duration Range (m)", 0.0, 20.0, 0.001)
    var pulseDistanceRange = DataModel(NumberRange(4.0, 6.0))

    // width
    @SeparatorParameter("Width", fontSize = 12.0)
    private var widthSep = Any()

    @Expose
    @SliderParameter("Pulse Width (m)", 0.0, 10.0, 0.001)
    var pulseWidth = DataModel(4.0f)

    @Expose
    @BooleanParameter("Randomize Pulse Width")
    var randomizePulseWidth = DataModel(true)

    @Expose
    @RangeSliderParameter("Pulse Width Range (m)", 0.0, 10.0, 0.001)
    var pulseWidthRange = DataModel(NumberRange(3.0, 4.0))

    // color
    @SeparatorParameter("Color", fontSize = 12.0)
    private var colorSep = Any()

    @Expose
    @RangeSliderParameter("Gradient Spectrum", 0.0, 1.0, 0.01)
    var gradientSpectrum = DataModel(NumberRange(0.0, 0.25))

    // expansion
    @SeparatorParameter("Expansion Curve", fontSize = 12.0)
    private var expansionSep = Any()

    @Expose
    @EnumParameter("Expansion Curve")
    var expansionCurve: DataModel<EasingMethod> = DataModel(EasingMethod.Linear)

    @Expose
    @BooleanParameter("Randomize Expansion Curve")
    var randomizeExpansionCurve = DataModel(true)
}