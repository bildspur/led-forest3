package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.ledforest.util.limit
import ch.bildspur.ledforest.util.modValue


class CloudScene(project: Project, tubes: List<Tube>, override val isInteracting: Boolean = true) : BaseInteractionScene("Cloud Scene", project, tubes) {
    private val task = TimerTask(project.cloudScene.timerInterval.value, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()
    var setupTimeStamp = 0L

    init {
        project.cloudScene.timerInterval.onChanged += {
            task.interval = it
        }
    }

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction }.toList()

        // set initial color
        iaTubes.forEachLED {
            it.color.fadeB(0f, 0.1f)
        }

        setupTimeStamp = System.currentTimeMillis()
    }

    private fun shiftedNoise(x: Float, y: Float, z: Float): Float {
        return Sketch.instance.noise(x, y, z)
    }

    override fun update() {
        val config = project.cloudScene

        // check if already is time else do nothing
        if(System.currentTimeMillis() - setupTimeStamp < config.initialWaitTime.value) {
            return
        }

        val time = Sketch.instance.millis() * config.noiseSpeed.value

        Sketch.instance.noiseDetail(config.lod.value, config.fallOff.value)

        iaTubes.forEach {
            it.leds.forEachIndexed { index, led ->
                val ledPosition = getLEDPosition(index, it)
                ledPosition.mult(config.scale.value)

                var modulator = (shiftedNoise(ledPosition.x, ledPosition.y, time) * config.contrast.value).limit(0.0f, 1.0f)
                modulator = config.mappingMode.value.method(modulator)

                if(config.enableFading.value) {
                    led.color.fadeH(config.hueSpectrum.value.modValue(modulator), config.fadeSpeed.value)
                    led.color.fadeS(config.saturationSpectrum.value.modValue(modulator), config.fadeSpeed.value)
                    led.color.fadeB(config.brightnessSpectrum.value.modValue(modulator), config.fadeSpeed.value)
                } else {
                    led.color.current.x = (config.hueSpectrum.value.modValue(modulator))
                    led.color.current.y = (config.saturationSpectrum.value.modValue(modulator))
                    led.color.current.z = (config.brightnessSpectrum.value.modValue(modulator))
                }
            }
        }
    }

    override fun stop() {

    }

    override fun dispose() {

    }
}