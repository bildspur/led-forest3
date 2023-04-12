package ch.bildspur.ledforest.util

import ch.bildspur.ledforest.model.easing.EasingMethod
import ch.bildspur.math.Float2
import ch.bildspur.math.Float3
import ch.bildspur.model.NumberRange
import com.google.gson.Gson
import processing.core.*
import processing.core.PConstants.QUAD_STRIP
import kotlin.math.PI


/**
 * Created by cansik on 04.02.17.
 */
fun Number.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun Float.isApproximate(value: Double, error: Double): Boolean {
    return (Math.abs(Math.abs(this) - Math.abs(value)) < error)
}

fun Float.limit(min: Float, max: Float): Float {
    return Math.max(Math.min(max, this), min)
}

fun Boolean.toFloat(): Float {
    return if (this) 1f else 0f
}

fun PVector.toFloat2(): Float2 {
    return Float2(this.x, this.y)
}

/**
 * Returns a single list of all elements from all arrays in the given array.
 */
fun <T> Array<out Array<out T>>.flatten(): List<T> {
    val result = ArrayList<T>(sumOf { it.size })
    for (element in this) {
        result.addAll(element)
    }
    return result
}

fun PGraphics.stackMatrix(block: (g: PGraphics) -> Unit) {
    this.pushMatrix()
    block(this)
    this.popMatrix()
}

fun PGraphics.draw(block: (g: PGraphics) -> Unit) {
    this.beginDraw()
    block(this)
    this.endDraw()
}

fun PGraphics.shape(block: (g: PGraphics) -> Unit) {
    this.beginShape()
    block(this)
    this.endShape(PApplet.CLOSE)
}

fun PGraphics.translate(vector: PVector) {
    this.translate(vector.x, vector.y, vector.z)
}

fun PGraphics.rotate(vector: PVector) {
    this.rotateX(vector.x)
    this.rotateY(vector.y)
    this.rotateZ(vector.z)
}


fun Float.toRadians(): Float {
    return PApplet.radians(this)
}

fun Float.toDegrees(): Float {
    return PApplet.degrees(this)
}

fun PGraphics.createRod(r: Float, h: Float, detail: Int): PShape {
    textureMode(PApplet.NORMAL)
    val sh = createShape()
    sh.beginShape(QUAD_STRIP)
    for (i in 0..detail) {
        val angle = PApplet.TWO_PI / detail
        val x = Math.sin((i * angle).toDouble()).toFloat()
        val z = Math.cos((i * angle).toDouble()).toFloat()
        val u = i.toFloat() / detail
        sh.normal(x, 0f, z)
        sh.vertex(x * r, -h / 2, z * r, u, 0f)
        sh.vertex(x * r, +h / 2, z * r, u, 1f)
    }
    sh.endShape()
    return sh
}

fun PGraphics.cross(x: Float, y: Float, size: Float) {
    this.line(x, y - size, x, y + size)
    this.line(x - size, y, x + size, y)
}

fun PGraphics.cylinder(sides: Int, r1: Float, r2: Float, h: Float) {
    val angle = (360 / sides).toFloat()
    val halfHeight = h / 2
    // top
    this.beginShape()
    for (i in 0 until sides) {
        val x = PApplet.cos(PApplet.radians(i * angle)) * r1
        val y = PApplet.sin(PApplet.radians(i * angle)) * r1
        this.vertex(x, y, -halfHeight)
    }
    this.endShape(PApplet.CLOSE)
    // bottom
    this.beginShape()
    for (i in 0 until sides) {
        val x = PApplet.cos(PApplet.radians(i * angle)) * r2
        val y = PApplet.sin(PApplet.radians(i * angle)) * r2
        this.vertex(x, y, halfHeight)
    }
    this.endShape(PApplet.CLOSE)
    // draw body
    this.beginShape(PApplet.TRIANGLE_STRIP)
    for (i in 0 until sides + 1) {
        val x1 = PApplet.cos(PApplet.radians(i * angle)) * r1
        val y1 = PApplet.sin(PApplet.radians(i * angle)) * r1
        val x2 = PApplet.cos(PApplet.radians(i * angle)) * r2
        val y2 = PApplet.sin(PApplet.radians(i * angle)) * r2
        this.vertex(x1, y1, -halfHeight)
        this.vertex(x2, y2, halfHeight)
    }
    this.endShape(PApplet.CLOSE)
}

fun PGraphics.imageRect(image: PImage, x: Float, y: Float, width: Float, height: Float) {
    val ratio = if (width - image.width < height - image.height) width / image.width else height / image.height
    this.image(image, x, y, image.width * ratio, image.height * ratio)
}

fun <T> Sequence<T>.batch(n: Int): Sequence<List<T>> {
    return BatchingSequence(this, n)
}

fun Boolean.toSign(): Int {
    return if (this) 1 else -1
}

fun Boolean.toInvertSign(): Int {
    return if (this) -1 else 1
}

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

fun PVector.flip(flipX: Boolean, flipY: Boolean, flipZ: Boolean) {
    this.x = this.x * flipX.toInvertSign()
    this.y = this.y * flipY.toInvertSign()
    this.z = this.z * flipZ.toInvertSign()
}

fun NumberRange.modValue(modulator: Float): Float {
    return ((this.high - this.low).toFloat() * modulator) + this.low.toFloat()
}

fun windowedSine(x: Float): Float {
    if (x < 0.0f || x > 1.0f)
        return 0.0f

    // calculate sine
    return (0.5 * (1 + kotlin.math.sin(2 * PI * x - (PI / 2)))).toFloat()
}

fun windowedSineIn(x: Float): Float {
    if (x < 0.0f || x > 1.0f)
        return 0.0f

    // calculate sine
    return (0.5 * (1 + kotlin.math.sin(PI * x - (PI / 2)))).toFloat()
}

fun windowedSineOut(x: Float): Float {
    if (x < 0.0f || x > 1.0f)
        return 0.0f

    // calculate sine
    return (0.5 * (1 + kotlin.math.sin(PI * x + (PI / 2)))).toFloat()
}

fun windowed(x: Float, lower: Float = 0.0f, upper: Float = 1.0f): Float {
    if (x < lower || x > upper)
        return 0.0f
    return x
}

fun windowedInOut(x: Float): Float {
    if (x < 0.0f || x > 1.0f)
        return 0.0f

    if (x > 0.5f)
        return (1.0f - x) * 2f

    return x * 2f
}

fun windowedMappedInOut(x: Float, inMapping: EasingMethod, outMapping: EasingMethod): Float {
    if (x < 0.0f || x > 1.0f)
        return 0.0f

    if (x > 0.5f)
        return outMapping.method((1.0f - x) * 2f)

    return inMapping.method(x * 2f)
}

inline fun <reified T : Any> T.deepCopy(obj: T): T {
    val json = Gson().toJson(obj)
    return Gson().fromJson(json, T::class.java)
}

fun Float2.distance(p2: Float2): Double {
    return Math.sqrt(Math.pow(p2.x - this.x.toDouble(), 2.0) + Math.pow(p2.y - this.y.toDouble(), 2.0))
}

fun Float3.distance(p2: Float3): Double {
    return Math.sqrt(
        Math.pow(p2.x - this.x.toDouble(), 2.0)
                + Math.pow(p2.y - this.y.toDouble(), 2.0)
                + Math.pow(p2.z - this.z.toDouble(), 2.0)
    )
}

fun <T> List<T>.center(): T? {
    if (this.isEmpty())
        return null
    return this[this.size / 2]
}