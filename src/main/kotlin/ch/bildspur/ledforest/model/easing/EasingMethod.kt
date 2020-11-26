package ch.bildspur.ledforest.model.easing

import ch.bildspur.ledforest.util.Easing
import com.jogamp.opengl.math.FloatUtil
import processing.core.PApplet
import processing.core.PConstants

enum class EasingMethod(val method : (Float) -> Float) {
    Linear({Easing.linear(it)}),
    EaseInSine({Easing.easeInSine(it)}),
    EaseOutSine({Easing.easeOutSine(it)}),
    EaseInOutSine({Easing.easeInOutSine(it)}),
    EaseInQuad({Easing.easeInQuad(it)}),
    EaseOutQuad({Easing.easeOutQuad(it)}),
    EaseInOutQuad({Easing.easeInOutQuad(it)}),
    EaseInCubic({Easing.easeInCubic(it)}),
    EaseOutCubic({Easing.easeOutCubic(it)}),
    EaseInOutCubic({Easing.easeInOutCubic(it)}),
    EaseInQuart({Easing.easeInQuart(it)}),
    EaseOutQuart({Easing.easeOutQuart(it)}),
    EaseInQuint({Easing.easeInQuint(it)}),
    EaseOutQuint({Easing.easeOutQuint(it)}),
    EaseInOutQuint({Easing.easeInOutQuint(it)}),
    EaseInCirc({Easing.easeInCirc(it)}),
    ;
}