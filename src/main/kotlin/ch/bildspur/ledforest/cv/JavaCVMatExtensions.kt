package ch.bildspur.ledforest.cv

import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_core.CV_32F
import org.bytedeco.opencv.global.opencv_core.CV_32S
import org.bytedeco.opencv.global.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun Mat.width(): Int {
    return this.cols()
}

fun Mat.height(): Int {
    return this.rows()
}

fun Mat.clear() {
    this.setTo(Mat(1, 1, opencv_core.CV_32SC4, Scalar.ALPHA255))
}

fun Mat.toMatOfPoint2f(): Mat {
    val dst = Mat()
    this.convertTo(dst, CV_32F)
    return dst
}

fun Mat.zeros(): Mat {
    return this.zeros(this.type())
}

fun Mat.zeros(type: Int): Mat {
    return Mat.zeros(this.rows(), this.cols(), type).asMat()
}

fun Mat.warpAffineCenter(rotation : Double, scale : Double) : Mat {
    val result = Mat(this.size(), this.type())
    val rawCenter = Point2f(this.cols() / 2.0f, this.rows() / 2.0f)

    val rotationMatrix = getRotationMatrix2D(rawCenter, rotation, scale)
    warpAffine(this, result, rotationMatrix, this.size())

    return result
}

fun Mat.convertColor(color: Int) {
    this.convertColor(this, color)
}

fun Mat.convertColor(target: Mat, color: Int) {
    cvtColor(this, target, color)
}

fun Mat.threshold(thresh: Double, maxval: Double = 255.0, type: Int = THRESH_BINARY) {
    threshold(this, this, thresh, maxval, type)
}

fun Mat.adaptiveThreshold(
    thresh: Double,
    maxval: Double = 255.0,
    adaptiveMethod: Int = CV_ADAPTIVE_THRESH_MEAN_C,
    type: Int = THRESH_BINARY,
    blockSize: Int = 5,
    constant: Double = 0.0
) {
    adaptiveThreshold(this, this, maxval, adaptiveMethod, type, blockSize, constant)
}

fun Mat.erode(erosionSize: Int) {
    if (erosionSize == 0)
        return

    val element = getStructuringElement(
        MORPH_RECT,
        Size((2.0 * erosionSize + 1.0).roundToInt(), (2.0 * erosionSize + 1.0).roundToInt())
    )
    erode(this, this, element)
    element.release()
}

fun Mat.dilate(dilationSize: Int) {
    if (dilationSize == 0)
        return

    val element = getStructuringElement(
        MORPH_RECT,
        Size((2.0 * dilationSize + 1.0).roundToInt(), (2.0 * dilationSize + 1.0).roundToInt())
    )
    dilate(this, this, element)
    element.release()
}

fun Mat.connectedComponents(connectivity: Int = 8, ltype: Int = CV_32S): Mat {
    val labeled = this.zeros()
    connectedComponents(this, labeled, connectivity, ltype)
    return labeled
}

fun Mat.imageCenter(): Point2d {
    return Point2d(this.cols() / 2.0, this.rows() / 2.0)
}

fun Point2d.transform(dx: Double, dy: Double): Point2d {
    return Point2d(this.x() + dx, this.y() + dy)
}

fun Point.transform(dx: Int, dy: Int): Point {
    return Point(this.x() + dx, this.y() + dy)
}

fun Mat.checkedROI(rect: Rect): Mat {
    // pre-check rectangle
    rect.x(max(0, min(this.cols(), rect.x())))
    rect.y(max(0, min(this.rows(), rect.y())))

    rect.width(if (this.cols() < rect.x() + rect.width()) this.cols() - rect.x() else rect.width())
    rect.height(if (this.rows() < rect.y() + rect.height()) this.rows() - rect.y() else rect.height())

    return Mat(this, rect)
}

fun Mat.directLocateROI(): Point {
    val size = Size()
    val point = Point()

    this.locateROI(size, point)

    return point
}

fun Mat.parentROI(): Rect {
    val location = this.directLocateROI()
    return Rect(location.x(), location.y(), this.cols(), this.rows())
}