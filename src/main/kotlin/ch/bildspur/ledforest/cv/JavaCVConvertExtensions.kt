package ch.bildspur.ledforest.cv

import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import org.bytedeco.javacv.OpenCVFrameConverter
import org.bytedeco.opencv.global.opencv_core.*
import org.bytedeco.opencv.opencv_core.IplImage
import org.bytedeco.opencv.opencv_core.Mat
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.*
import java.nio.ByteBuffer


private var matConverter = OpenCVFrameConverter.ToMat()

fun Frame.toMat(): Mat {
    return matConverter.convert(this)
}

fun Mat.toFrame(): Frame {
    return matConverter.convert(this)
}

fun Frame.toIplImage(): IplImage {
    return matConverter.convertToIplImage(this)
}

fun IplImage.toBufferedImage(): BufferedImage {
    val grabberConverter = OpenCVFrameConverter.ToIplImage()
    val paintConverter = Java2DFrameConverter()
    val frame = grabberConverter.convert(this)
    return paintConverter.getBufferedImage(frame, 1.0)
}

fun IplImage.toFrame(): Frame {
    return matConverter.convert(this)
}

fun Mat.toOpenCVMat(): org.opencv.core.Mat {
    return org.opencv.core.Mat(this.address())
}

fun Frame.toOpenCVMat(): org.opencv.core.Mat {
    return matConverter.convertToOrgOpenCvCoreMat(this)
}

fun Frame.createBufferedImageFast(image: BufferedImage?): BufferedImage {
    val buffer = this.image[0].position(0) as ByteBuffer

    if (image == null) {
        val cs = ColorSpace.getInstance(ColorSpace.CS_sRGB)

        val cm = ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE)
        // this assumes BGR format
        val dataBuffer = DataBufferByte(buffer.limit())
        val wr = Raster.createWritableRaster(
            ComponentSampleModel(
                DataBuffer.TYPE_BYTE,
                this.imageWidth,
                this.imageHeight,
                this.imageChannels,
                this.imageStride,
                intArrayOf(2, 1, 0)
            ), dataBuffer, null
        )
        val bufferPixels = (wr.dataBuffer as DataBufferByte).data

        buffer.get(bufferPixels)

        return BufferedImage(cm, wr, false, null)
    } else {
        val wr = image.raster
        val bufferPixels = (wr.dataBuffer as DataBufferByte).data

        buffer.get(bufferPixels)

        return image
    }
}

fun BufferedImage.createFrameFast(frame: Frame?): Frame {
    var frame = frame
    val imageBuffer = this.raster.dataBuffer as DataBufferByte
    val stride = imageBuffer.data.size / this.height

    if (frame == null || frame.imageWidth != this.width || frame.imageHeight != this.height)
        frame = Frame(this.width, this.height, 8, 3, stride)

    val frameBuffer = frame.image[0].position(0) as ByteBuffer

    frameBuffer.put((this.raster.dataBuffer as DataBufferByte).data)
    frameBuffer.position(0)

    return frame
}

fun org.opencv.core.Mat.toJavaCVMat(): Mat {
    return Mat(this.nativeObjAddr)
}

/*
fun PImage.toMat(m: org.opencv.core.Mat) {
    val matPixels = ((this.native as BufferedImage).raster.dataBuffer as DataBufferInt).data

    val bb = ByteBuffer.allocate(matPixels.size * 4)
    val ib = bb.asIntBuffer()
    ib.put(matPixels)

    val bvals = bb.array()

    m.put(0, 0, bvals)
}

fun org.opencv.core.Mat.toPImage(img: PImage) {
    img.loadPixels()

    when {
        this.channels() == 3 -> {
            val m2 = org.opencv.core.Mat()
            Imgproc.cvtColor(this, m2, Imgproc.COLOR_RGB2RGBA)
            img.pixels = m2.toARGBPixels()
        }
        this.channels() == 1 -> {
            val m2 = org.opencv.core.Mat()
            Imgproc.cvtColor(this, m2, Imgproc.COLOR_GRAY2RGBA)
            img.pixels = m2.toARGBPixels()
        }
        this.channels() == 4 -> img.pixels = this.toARGBPixels()
    }

    img.updatePixels()
}

fun org.opencv.core.Mat.toARGBPixels(): IntArray {
    val pImageChannels = 4
    val numPixels = this.width() * this.height()
    val intPixels = IntArray(numPixels)
    val matPixels = ByteArray(numPixels * pImageChannels)

    this.get(0, 0, matPixels)
    ByteBuffer.wrap(matPixels).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(intPixels)
    return intPixels
}
*/

fun Mat.to8U() {
    this.convertTo(this, CV_8U)
}

fun Mat.to8UC1() {
    this.convertTo(this, CV_8UC1)
}

fun Mat.to8UC3() {
    this.convertTo(this, CV_8UC3)
}

fun Mat.to32S() {
    this.convertTo(this, CV_32S)
}

fun Mat.to32SC1() {
    this.convertTo(this, CV_32SC1)
}

fun Mat.to32SC3() {
    this.convertTo(this, CV_32SC3)
}

fun Mat.to32FC2() {
    this.convertTo(this, CV_32FC2)
}