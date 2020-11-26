package ch.bildspur.ledforest.scene

import ch.bildspur.ledforest.Sketch
import ch.bildspur.ledforest.controller.timer.TimerTask
import ch.bildspur.ledforest.model.Project
import ch.bildspur.ledforest.model.light.Tube
import ch.bildspur.ledforest.model.light.TubeTag
import ch.bildspur.ledforest.util.Easing
import ch.bildspur.ledforest.util.forEachLED
import ch.bildspur.ledforest.util.limit
import ch.bildspur.model.NumberRange
import processing.core.PVector


class CloudScene(project: Project, tubes: List<Tube>, override val isInteracting: Boolean = true) : BaseInteractionScene("Cloud Scene", project, tubes) {
    private val task = TimerTask(project.cloudScene.timerInterval.value, { update() })

    override val timerTask: TimerTask
        get() = task

    var iaTubes = emptyList<Tube>()

    init {
        project.cloudScene.timerInterval.onChanged += {
            task.interval = it
        }
    }

    override fun setup() {
        iaTubes = tubes.filter { it.tag.value == TubeTag.Interaction }.toList()

        // set initial color
        iaTubes.forEachLED {
            it.color.fadeH(project.cloudScene.hueSpectrum.value.low.toFloat(), 0.05f)
            it.color.fadeS(project.cloudScene.saturationSpectrum.value.low.toFloat(), 0.05f)
            it.color.fadeB(0f, 0.05f)
        }
    }

    private fun shiftedNoise(position: PVector, dx: Float, dy: Float, dz: Float): Float {
        return Sketch.instance.noise(position.x + dx, position.y + dy, position.z + dz)
    }

    override fun update() {
        val config = project.cloudScene
        val time = Sketch.instance.millis() * config.noiseSpeed.value

        Sketch.instance.noiseDetail(config.lod.value, config.fallOff.value)

        iaTubes.forEach {
            it.leds.forEachIndexed { index, led ->
                val ledPosition = getLEDPosition(index, it)
                ledPosition.mult(config.scale.value)

                val dx = if(config.modX.value) time else 0f
                val dy = if(config.modY.value) time else 0f
                val dz = if(config.modZ.value) time else 0f

                var modulator = (shiftedNoise(ledPosition, dx, dy, dz) * config.contrast.value).limit(0.0f, 1.0f)

                if(config.modEasing.value) {
                    modulator = Easing.easeInSine(modulator)
                }

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

    private fun NumberRange.modValue(modulator: Float): Float {
        return ((this.high - this.low).toFloat() * modulator) + this.low.toFloat()
    }
}