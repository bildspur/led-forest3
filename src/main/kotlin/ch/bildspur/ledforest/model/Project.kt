package ch.bildspur.ledforest.model

import ch.bildspur.color.RGB
import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.model.interaction.Interaction
import ch.bildspur.ledforest.model.interaction.LeapInteraction
import ch.bildspur.ledforest.model.interaction.PoseInteraction
import ch.bildspur.ledforest.model.interaction.RealSenseInteraction
import ch.bildspur.ledforest.model.leda.LedaConfig
import ch.bildspur.ledforest.model.light.DmxNode
import ch.bildspur.ledforest.model.light.LightElement
import ch.bildspur.ledforest.model.light.SpatialLightElement
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.ui.control.tubemap.shape.TubeShape
import ch.bildspur.ledforest.ui.properties.ArrowControlParameter
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.model.DataModel
import ch.bildspur.ui.properties.*
import com.google.gson.annotations.Expose
import javafx.scene.input.KeyCode
import processing.core.PApplet
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.max
import kotlin.math.round

/**
 * Created by cansik on 11.07.17.
 */
class Project {
    @Expose
    @StringParameter("Name")
    var name = DataModel("${Sketch.NAME} Project")

    @StringParameter("Active Scene", isEditable = false)
    var activeScene = DataModel("-")

    @Expose
    @BooleanParameter("Scene Manager Enabled")
    var isSceneManagerEnabled = DataModel(true)

    @ActionParameter("LED Tubes", "Rename")
    var autoNameLEDTubes = {
        tubes.groupBy { it.universe.value }.forEach { (u, ts) ->
            ts.forEachIndexed { i, t -> t.name.value = "${(65 + u).toChar()}${i + 1}" }
        }
        println("renamed tubes!")
    }

    @LabelParameter("Global Settings")

    @ColorParameter("Solid LED Color")
    var solidLEDColor = DataModel(RGB(1.0, 0.0, 0.0, 1.0))

    @ActionParameter("LEDs", "On")
    var turnOnLEDs = {
        if (isSceneManagerEnabled.value) {
            isSceneManagerEnabled.value = false
            Thread.sleep(50)
        }

        Sketch.instance.project.value.lightElements.forEachLED {
            it.color.fadeB(100.0f, 0.1f)
        }
    }

    @ActionParameter("LEDs", "Off")
    var turnOffLEDs = {
        if (isSceneManagerEnabled.value) {
            isSceneManagerEnabled.value = false
            Thread.sleep(50)
        }

        Sketch.instance.project.value.lightElements.forEachLED {
            it.color.fadeB(0.0f, 0.1f)
        }
    }

    @ActionParameter("Mapping", "Test Color")
    var mappingTestColor = {
        if (isSceneManagerEnabled.value) {
            isSceneManagerEnabled.value = false
            Thread.sleep(50)
        }

        val universes = Sketch.instance.project.value.lightElements.groupBy { it.universe.value }
        universes.forEach { (u, ts) ->
            val color = TubeShape.UNIVERSE_COLORS[u % TubeShape.UNIVERSE_COLORS.size]
            val minAddress = ts.minOf { it.startAddress }
            val maxAddress = ts.maxOf { it.endAddress }

            ts.forEach { tube ->
                tube.leds.forEachIndexed { index, led ->
                    var hue = color.hue.toFloat()
                    var saturation = Sketch.map(led.address, minAddress, maxAddress, 0, 200).toFloat()

                    if (saturation >= 100) {
                        saturation = 200 - saturation;
                    }

                    // set first and last color
                    if (index == 0 || index == tube.leds.count() - 1) {
                        hue = color.invert().hue.toFloat()
                        saturation = 100f
                    }

                    led.color.fadeH(hue, 0.2f)
                    led.color.fadeS(saturation, 0.2f)
                    led.color.fadeB(100f, 0.2f)
                }
            }
        }

        println("mapping color activated")
    }

    @ArrowControlParameter("Translate All")
    var translateAll: (KeyCode) -> Unit = { code ->
        val adjustAmount = 0.1f
        Sketch.instance.project.value.spatialLightElements.forEach {
            when (code) {
                KeyCode.UP -> it.position.value.y -= adjustAmount
                KeyCode.DOWN -> it.position.value.y += adjustAmount
                KeyCode.LEFT -> it.position.value.x -= adjustAmount
                KeyCode.RIGHT -> it.position.value.x += adjustAmount
                else -> println("not valid key!")
            }
        }
    }

    @Expose
    @GroupParameter("FireLog", expanded = false)
    var fireLog = FireLogConfig()

    @Expose
    @GroupParameter("Supabase", expanded = false)
    var supabase = SupabaseConfig()

    @Expose
    var nodes = CopyOnWriteArrayList<DmxNode>()

    @Expose
    var tubes = CopyOnWriteArrayList<Tube>()

    @Expose
    var lights = CopyOnWriteArrayList<LightElement>()

    @Expose
    var interaction = Interaction()

    @Expose
    var leapInteraction = LeapInteraction()

    @Expose
    var realSenseInteraction = RealSenseInteraction()

    @Expose
    var poseInteraction = PoseInteraction()

    @Expose
    var starPattern = StarPatternConfig()

    @Expose
    var cloudScene = CloudSceneConfig()

    @Expose
    var pulseScene = PulseSceneConfig()

    @Expose
    var leda = LedaConfig()

    @Expose
    var visualisation = Visualisation()

    @Expose
    var light = Light()

    @Expose
    var map = Map()

    @Expose
    var audio = Audio()

    @Expose
    var ui = UserInterfaceConfig()

    @Expose
    var test = TestConfig()

    init {
        solidLEDColor.onChanged += {
            isSceneManagerEnabled.value = false
        }
    }

    val lightElements: List<LightElement>
        get() = tubes + lights

    val spatialLightElements: List<SpatialLightElement>
        get() = lightElements.filterIsInstance(SpatialLightElement::class.java)
}