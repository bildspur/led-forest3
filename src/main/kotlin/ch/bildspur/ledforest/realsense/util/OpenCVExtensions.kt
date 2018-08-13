package ch.bildspur.ledforest.realsense.util

import ch.bildspur.ledforest.realsense.vision.ConnectedComponentsResult
import org.opencv.core.*
import org.opencv.core.Core.FONT_HERSHEY_SCRIPT_SIMPLEX
import org.opencv.imgproc.Imgproc
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PImage
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


/**
 * Created by cansik on 04.02.17.
 */

fun Float.isApproximate(value: Double, error: Double): Boolean {
    return (Math.abs(Math.abs(this) - Math.abs(value)) < error)
}

fun DoubleArray.indexOfMax(): Int? {
    return this.withIndex().maxBy { it.value }?.index
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

fun PGraphics.cross(x: Float, y: Float, size: Float) {
    this.line(x, y - size, x, y + size)
    this.line(x - size, y, x + size, y)
}

fun PImage.toMat(): Mat {
    val image = Mat(this.height, this.width, CvType.CV_8UC4)
    this.toMat(image)
    return image
}

fun PImage.toMat(m: Mat) {
    val matPixels = ((this.native as BufferedImage).raster.dataBuffer as DataBufferInt).data

    val bb = ByteBuffer.allocate(matPixels.size * 4)
    val ib = bb.asIntBuffer()
    ib.put(matPixels)

    val bvals = bb.array()

    m.put(0, 0, bvals)
}

fun Mat.toPImage(img: PImage) {
    img.loadPixels()

    when {
        this.channels() == 3 -> {
            val m2 = Mat()
            Imgproc.cvtColor(this, m2, Imgproc.COLOR_RGB2RGBA)
            img.pixels = m2.toARGBPixels()
        }
        this.channels() == 1 -> {
            val m2 = Mat()
            Imgproc.cvtColor(this, m2, Imgproc.COLOR_GRAY2RGBA)
            img.pixels = m2.toARGBPixels()
        }
        this.channels() == 4 -> img.pixels = this.toARGBPixels()
    }

    img.updatePixels()
}

fun Mat.toPImage(): PImage {
    val resultImage = PImage(this.cols(), this.rows())
    this.toPImage(resultImage)
    return resultImage
}

fun Mat.toARGBPixels(): IntArray {
    val pImageChannels = 4
    val numPixels = this.width() * this.height()
    val intPixels = IntArray(numPixels)
    val matPixels = ByteArray(numPixels * pImageChannels)

    this.get(0, 0, matPixels)
    ByteBuffer.wrap(matPixels).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(intPixels)
    return intPixels
}

fun Mat.toBGRA(bgra: Mat) {
    val channels = ArrayList<Mat>()
    Core.split(this, channels)

    val reordered = ArrayList<Mat>()
    // Starts as ARGB.
    // Make into BGRA.

    reordered.add(channels[3])
    reordered.add(channels[2])
    reordered.add(channels[1])
    reordered.add(channels[0])

    Core.merge(reordered, bgra)
}

/*
fun Mat.toImage(): Image {
    val byteMat = MatOfByte()
    Imgcodecs.imencode(".bmp", this, byteMat)
    return Image(ByteArrayInputStream(byteMat.toArray()))
}
*/

fun Mat.zeros(): Mat {
    return this.zeros(this.type())
}

fun Mat.zeros(type: Int): Mat {
    return Mat.zeros(this.rows(), this.cols(), type)
}

fun Mat.copy(): Mat {
    val m = this.zeros()
    this.copyTo(m)
    return m
}

fun Mat.resize(width: Int, height: Int): Mat {
    assert(width > 0 || height > 0)

    var w = width
    var h = height

    if (width == 0) {
        w = ((height.toDouble() / this.height()) * this.width()).toInt()
    }

    if (height == 0) {
        h = ((width.toDouble() / this.width()) * this.height()).toInt()
    }

    val result = Mat.zeros(h, w, this.type())
    Imgproc.resize(this, result, result.size())
    return result
}

fun Mat.geodesicDilate(mask: Mat, elementSize: Int) {
    this.geodesicDilate(mask, elementSize, this)
}

fun Mat.geodesicDilate(mask: Mat, elementSize: Int, dest: Mat) {
    val img = this.clone()
    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * elementSize + 1.0, 2.0 * elementSize + 1.0))

    var last = img.zeros()
    val next = img.copy()
    do {
        last = next.copy()
        Imgproc.dilate(last, next, element)
        Core.min(next, mask, next)
    } while (Core.norm(last, next) > 0.0001)

    last.copyTo(dest)
}

fun Mat.geodesicErode(mask: Mat, elementSize: Int) {
    this.geodesicErode(mask, elementSize, this)
}

fun Mat.geodesicErode(mask: Mat, elementSize: Int, dest: Mat) {
    val img = this.clone()
    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * elementSize + 1.0, 2.0 * elementSize + 1.0))

    Imgproc.dilate(this, img, element)
    Core.min(img, mask, img)
    img.copyTo(dest)
}

fun Mat.negate() {
    this.negate(this)
}

fun Mat.negate(dest: Mat) {
    val invertedColorMatrix = this.zeros().setTo(Scalar(255.0))
    Core.subtract(invertedColorMatrix, this, dest)
}

fun Mat.gray() {
    Imgproc.cvtColor(this, this, Imgproc.COLOR_BGR2GRAY)
}

fun Mat.erode(erosionSize: Int) {
    if (erosionSize == 0)
        return

    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * erosionSize + 1.0, 2.0 * erosionSize + 1.0))
    Imgproc.erode(this, this, element)
    element.release()
}

fun Mat.dilate(dilationSize: Int) {
    if (dilationSize == 0)
        return

    val element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0 * dilationSize + 1.0, 2.0 * dilationSize + 1.0))
    Imgproc.dilate(this, this, element)
    element.release()
}

fun Mat.threshold(thresh: Double, maxval: Double = 255.0, type: Int = Imgproc.THRESH_BINARY) {
    Imgproc.threshold(this, this, thresh, maxval, type)
}

fun Mat.getRegionMask(regionLabel: Int): Mat {
    val labeledMask = this.zeros(CvType.CV_8U)
    Core.inRange(this, Scalar(regionLabel.toDouble()), Scalar(regionLabel.toDouble()), labeledMask)
    return labeledMask
}

fun PGraphics.imageRect(image: PImage, x: Float, y: Float, width: Float, height: Float) {
    val ratio = if (width - image.width < height - image.height) width / image.width else height / image.height
    this.image(image, x, y, image.width * ratio, image.height * ratio)
}

fun MatOfPoint.convexHull(clockwise: Boolean = false): MatOfInt {
    val result = MatOfInt()
    Imgproc.convexHull(this, result, clockwise)
    return result
}

fun Mat.drawCircle(center: Point, radius: Int, color: Scalar, thickness: Int = 1) {
    Imgproc.circle(this, center, radius, color, thickness)
}

fun Mat.drawCross(center: Point, size: Double, color: Scalar, thickness: Int = 1) {
    val length = size / 2.0
    Imgproc.line(this, Point(center.x - length, center.y), Point(center.x + length, center.y), color, thickness)
    Imgproc.line(this, Point(center.x, center.y - length), Point(center.x, center.y + length), color, thickness)
}

fun Mat.drawText(text: String, position: Point, color: Scalar, thickness: Int = 1, fontFace: Int = FONT_HERSHEY_SCRIPT_SIMPLEX, scale: Double = 1.0) {
    Imgproc.putText(this, text, position, fontFace, scale, color, thickness)
}

fun Mat.imageCenter(): Point {
    return Point(this.width() / 2.0, this.height() / 2.0)
}

fun Point.transform(dx: Double, dy: Double): Point {
    return Point(this.x + dx, this.y + dy)
}

fun Mat.connectedComponents(connectivity: Int = 8, ltype: Int = CvType.CV_32S): Mat {
    val labeled = this.zeros()
    Imgproc.connectedComponents(this, labeled, connectivity, ltype)
    return labeled
}

fun Mat.connectedComponentsWithStats(connectivity: Int = 8, ltype: Int = CvType.CV_32S): ConnectedComponentsResult {
    val labeled = this.zeros()
    val rectComponents = Mat()
    val centComponents = Mat()

    Imgproc.connectedComponentsWithStats(this, labeled, rectComponents, centComponents)
    return ConnectedComponentsResult(labeled, rectComponents, centComponents)
}