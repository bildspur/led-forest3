package ch.bildspur.ledforest.model.easing

import ch.bildspur.ledforest.util.EasingCurves

enum class EasingMethod(val method : (Float) -> Float) {
    Linear({EasingCurves.linear(it)}),
    Step({EasingCurves.step(it)}),
    EaseInSine({EasingCurves.easeInSine(it)}),
    EaseOutSine({EasingCurves.easeOutSine(it)}),
    EaseInOutSine({EasingCurves.easeInOutSine(it)}),
    EaseInQuad({EasingCurves.easeInQuad(it)}),
    EaseOutQuad({EasingCurves.easeOutQuad(it)}),
    EaseInOutQuad({EasingCurves.easeInOutQuad(it)}),
    EaseInCubic({EasingCurves.easeInCubic(it)}),
    EaseOutCubic({EasingCurves.easeOutCubic(it)}),
    EaseInOutCubic({EasingCurves.easeInOutCubic(it)}),
    EaseInQuart({EasingCurves.easeInQuart(it)}),
    EaseOutQuart({EasingCurves.easeOutQuart(it)}),
    EaseInOutQuart({EasingCurves.easeInOutQuart(it)}),
    EaseInQuint({EasingCurves.easeInQuint(it)}),
    EaseOutQuint({EasingCurves.easeOutQuint(it)}),
    EaseInOutQuint({EasingCurves.easeInOutQuint(it)}),
    EaseInCirc({EasingCurves.easeInCirc(it)}),
    ;
}