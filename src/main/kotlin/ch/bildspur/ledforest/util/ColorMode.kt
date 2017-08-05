package ch.bildspur.ledforest.util

import processing.core.PApplet
import java.awt.Color

object ColorMode {
    var colorMode: Int = 0
    var colorModeX: Float = 0.toFloat()
    var colorModeY: Float = 0.toFloat()
    var colorModeZ: Float = 0.toFloat()
    var colorModeA: Float = 0.toFloat()
    internal var colorModeScale: Boolean = false
    internal var colorModeDefault: Boolean = false

    internal var calcR: Float = 0.toFloat()
    internal var calcG: Float = 0.toFloat()
    internal var calcB: Float = 0.toFloat()
    internal var calcA: Float = 0.toFloat()
    internal var calcRi: Int = 0
    internal var calcGi: Int = 0
    internal var calcBi: Int = 0
    internal var calcAi: Int = 0
    internal var calcColor: Int = 0
    internal var calcAlpha: Boolean = false
    internal var cacheHsbKey: Int = 0
    internal var cacheHsbValue = FloatArray(3)

    internal var lerpColorHSB1: FloatArray? = null
    internal lateinit var lerpColorHSB2: FloatArray

    fun colorMode(mode: Int) {
        this.colorMode(mode, this.colorModeX, this.colorModeY, this.colorModeZ, this.colorModeA)
    }

    fun colorMode(mode: Int, max: Float) {
        this.colorMode(mode, max, max, max, max)
    }

    fun colorMode(mode: Int, max1: Float, max2: Float, max3: Float) {
        this.colorMode(mode, max1, max2, max3, this.colorModeA)
    }

    fun colorMode(mode: Int, max1: Float, max2: Float, max3: Float, maxA: Float) {
        this.colorMode = mode
        this.colorModeX = max1
        this.colorModeY = max2
        this.colorModeZ = max3
        this.colorModeA = maxA
        this.colorModeScale = maxA != 1.0f || max1 != max2 || max2 != max3 || max3 != maxA
        this.colorModeDefault = this.colorMode == 1 && this.colorModeA == 255.0f && this.colorModeX == 255.0f && this.colorModeY == 255.0f && this.colorModeZ == 255.0f
    }

    internal fun colorCalc(rgb: Int) {
        if (rgb and -16777216 == 0 && rgb.toFloat() <= this.colorModeX) {
            this.colorCalc(rgb.toFloat())
        } else {
            this.colorCalcARGB(rgb, this.colorModeA)
        }

    }

    internal fun colorCalc(rgb: Int, alpha: Float) {
        if (rgb and -16777216 == 0 && rgb.toFloat() <= this.colorModeX) {
            this.colorCalc(rgb.toFloat(), alpha)
        } else {
            this.colorCalcARGB(rgb, alpha)
        }

    }

    internal fun colorCalc(gray: Float) {
        this.colorCalc(gray, this.colorModeA)
    }

    internal fun colorCalc(gray: Float, alpha: Float) {
        var gray = gray
        var alpha = alpha
        if (gray > this.colorModeX) {
            gray = this.colorModeX
        }

        if (alpha > this.colorModeA) {
            alpha = this.colorModeA
        }

        if (gray < 0.0f) {
            gray = 0.0f
        }

        if (alpha < 0.0f) {
            alpha = 0.0f
        }

        this.calcR = if (this.colorModeScale) gray / this.colorModeX else gray
        this.calcG = this.calcR
        this.calcB = this.calcR
        this.calcA = if (this.colorModeScale) alpha / this.colorModeA else alpha
        this.calcRi = (this.calcR * 255.0f).toInt()
        this.calcGi = (this.calcG * 255.0f).toInt()
        this.calcBi = (this.calcB * 255.0f).toInt()
        this.calcAi = (this.calcA * 255.0f).toInt()
        this.calcColor = this.calcAi shl 24 or (this.calcRi shl 16) or (this.calcGi shl 8) or this.calcBi
        this.calcAlpha = this.calcAi != 255
    }

    internal fun colorCalc(x: Float, y: Float, z: Float) {
        this.colorCalc(x, y, z, this.colorModeA)
    }

    internal fun colorCalc(x: Float, y: Float, z: Float, a: Float) {
        var x = x
        var y = y
        var z = z
        var a = a
        if (x > this.colorModeX) {
            x = this.colorModeX
        }

        if (y > this.colorModeY) {
            y = this.colorModeY
        }

        if (z > this.colorModeZ) {
            z = this.colorModeZ
        }

        if (a > this.colorModeA) {
            a = this.colorModeA
        }

        if (x < 0.0f) {
            x = 0.0f
        }

        if (y < 0.0f) {
            y = 0.0f
        }

        if (z < 0.0f) {
            z = 0.0f
        }

        if (a < 0.0f) {
            a = 0.0f
        }

        when (this.colorMode) {
            1 -> if (this.colorModeScale) {
                this.calcR = x / this.colorModeX
                this.calcG = y / this.colorModeY
                this.calcB = z / this.colorModeZ
                this.calcA = a / this.colorModeA
            } else {
                this.calcR = x
                this.calcG = y
                this.calcB = z
                this.calcA = a
            }
            3 -> {
                x /= this.colorModeX
                y /= this.colorModeY
                z /= this.colorModeZ
                this.calcA = if (this.colorModeScale) a / this.colorModeA else a
                if (y == 0.0f) {
                    this.calcB = z
                    this.calcG = this.calcB
                    this.calcR = this.calcG
                } else {
                    val which = (x - x.toInt().toFloat()) * 6.0f
                    val f = which - which.toInt().toFloat()
                    val p = z * (1.0f - y)
                    val q = z * (1.0f - y * f)
                    val t = z * (1.0f - y * (1.0f - f))
                    when (which.toInt()) {
                        0 -> {
                            this.calcR = z
                            this.calcG = t
                            this.calcB = p
                        }
                        1 -> {
                            this.calcR = q
                            this.calcG = z
                            this.calcB = p
                        }
                        2 -> {
                            this.calcR = p
                            this.calcG = z
                            this.calcB = t
                        }
                        3 -> {
                            this.calcR = p
                            this.calcG = q
                            this.calcB = z
                        }
                        4 -> {
                            this.calcR = t
                            this.calcG = p
                            this.calcB = z
                        }
                        5 -> {
                            this.calcR = z
                            this.calcG = p
                            this.calcB = q
                        }
                    }
                }
            }
        }

        this.calcRi = (255.0f * this.calcR).toInt()
        this.calcGi = (255.0f * this.calcG).toInt()
        this.calcBi = (255.0f * this.calcB).toInt()
        this.calcAi = (255.0f * this.calcA).toInt()
        this.calcColor = this.calcAi shl 24 or (this.calcRi shl 16) or (this.calcGi shl 8) or this.calcBi
        this.calcAlpha = this.calcAi != 255
    }

    internal fun colorCalcARGB(argb: Int, alpha: Float) {
        if (alpha == this.colorModeA) {
            this.calcAi = argb shr 24 and 255
            this.calcColor = argb
        } else {
            this.calcAi = ((argb shr 24 and 255).toFloat() * PApplet.constrain(alpha / this.colorModeA, 0.0f, 1.0f)).toInt()
            this.calcColor = this.calcAi shl 24 or (argb and 16777215)
        }

        this.calcRi = argb shr 16 and 255
        this.calcGi = argb shr 8 and 255
        this.calcBi = argb and 255
        this.calcA = this.calcAi.toFloat() / 255.0f
        this.calcR = this.calcRi.toFloat() / 255.0f
        this.calcG = this.calcGi.toFloat() / 255.0f
        this.calcB = this.calcBi.toFloat() / 255.0f
        this.calcAlpha = this.calcAi != 255
    }

    fun color(c: Int): Int {
        this.colorCalc(c)
        return this.calcColor
    }

    fun color(gray: Float): Int {
        this.colorCalc(gray)
        return this.calcColor
    }

    fun color(c: Int, alpha: Int): Int {
        this.colorCalc(c, alpha.toFloat())
        return this.calcColor
    }

    fun color(c: Int, alpha: Float): Int {
        this.colorCalc(c, alpha)
        return this.calcColor
    }

    fun color(gray: Float, alpha: Float): Int {
        this.colorCalc(gray, alpha)
        return this.calcColor
    }

    fun color(v1: Int, v2: Int, v3: Int): Int {
        this.colorCalc(v1.toFloat(), v2.toFloat(), v3.toFloat())
        return this.calcColor
    }

    fun color(v1: Float, v2: Float, v3: Float): Int {
        this.colorCalc(v1, v2, v3)
        return this.calcColor
    }

    fun color(v1: Int, v2: Int, v3: Int, a: Int): Int {
        this.colorCalc(v1.toFloat(), v2.toFloat(), v3.toFloat(), a.toFloat())
        return this.calcColor
    }

    fun color(v1: Float, v2: Float, v3: Float, a: Float): Int {
        this.colorCalc(v1, v2, v3, a)
        return this.calcColor
    }

    fun alpha(rgb: Int): Float {
        val outgoing = (rgb shr 24 and 255).toFloat()
        return if (this.colorModeA == 255.0f) outgoing else outgoing / 255.0f * this.colorModeA
    }

    fun red(rgb: Int): Float {
        val c = (rgb shr 16 and 255).toFloat()
        return if (this.colorModeDefault) c else c / 255.0f * this.colorModeX
    }

    fun green(rgb: Int): Float {
        val c = (rgb shr 8 and 255).toFloat()
        return if (this.colorModeDefault) c else c / 255.0f * this.colorModeY
    }

    fun blue(rgb: Int): Float {
        val c = (rgb and 255).toFloat()
        return if (this.colorModeDefault) c else c / 255.0f * this.colorModeZ
    }

    fun hue(rgb: Int): Float {
        if (rgb != this.cacheHsbKey) {
            Color.RGBtoHSB(rgb shr 16 and 255, rgb shr 8 and 255, rgb and 255, this.cacheHsbValue)
            this.cacheHsbKey = rgb
        }

        return this.cacheHsbValue[0] * this.colorModeX
    }

    fun saturation(rgb: Int): Float {
        if (rgb != this.cacheHsbKey) {
            Color.RGBtoHSB(rgb shr 16 and 255, rgb shr 8 and 255, rgb and 255, this.cacheHsbValue)
            this.cacheHsbKey = rgb
        }

        return this.cacheHsbValue[1] * this.colorModeY
    }

    fun brightness(rgb: Int): Float {
        if (rgb != this.cacheHsbKey) {
            Color.RGBtoHSB(rgb shr 16 and 255, rgb shr 8 and 255, rgb and 255, this.cacheHsbValue)
            this.cacheHsbKey = rgb
        }

        return this.cacheHsbValue[2] * this.colorModeZ
    }

    fun lerpColor(c1: Int, c2: Int, amt: Float): Int {
        return lerpColor(c1, c2, amt, this.colorMode)
    }

    fun lerpColor(c1: Int, c2: Int, amt: Float, mode: Int): Int {
        var amt = amt
        if (amt < 0.0f) {
            amt = 0.0f
        }

        if (amt > 1.0f) {
            amt = 1.0f
        }

        val a1: Float
        val a2: Float
        val ho: Float
        val so: Float
        val bo: Float
        if (mode == 1) {
            a1 = (c1 shr 24 and 255).toFloat()
            a2 = (c1 shr 16 and 255).toFloat()
            val g1 = (c1 shr 8 and 255).toFloat()
            ho = (c1 and 255).toFloat()
            so = (c2 shr 24 and 255).toFloat()
            bo = (c2 shr 16 and 255).toFloat()
            val g2 = (c2 shr 8 and 255).toFloat()
            val b2 = (c2 and 255).toFloat()
            return PApplet.round(a1 + (so - a1) * amt) shl 24 or (PApplet.round(a2 + (bo - a2) * amt) shl 16) or (PApplet.round(g1 + (g2 - g1) * amt) shl 8) or PApplet.round(ho + (b2 - ho) * amt)
        } else if (mode == 3) {
            if (lerpColorHSB1 == null) {
                lerpColorHSB1 = FloatArray(3)
                lerpColorHSB2 = FloatArray(3)
            }

            a1 = (c1 shr 24 and 255).toFloat()
            a2 = (c2 shr 24 and 255).toFloat()
            val alfa = PApplet.round(a1 + (a2 - a1) * amt) shl 24
            Color.RGBtoHSB(c1 shr 16 and 255, c1 shr 8 and 255, c1 and 255, lerpColorHSB1)
            Color.RGBtoHSB(c2 shr 16 and 255, c2 shr 8 and 255, c2 and 255, lerpColorHSB2)
            ho = PApplet.lerp(lerpColorHSB1!![0], lerpColorHSB2[0], amt)
            so = PApplet.lerp(lerpColorHSB1!![1], lerpColorHSB2[1], amt)
            bo = PApplet.lerp(lerpColorHSB1!![2], lerpColorHSB2[2], amt)
            return alfa or (Color.HSBtoRGB(ho, so, bo) and 16777215)
        } else {
            return 0
        }
    }

}