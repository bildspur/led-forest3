package ch.bildspur.ledforest.realsense.vision

import ch.bildspur.ledforest.realsense.util.*
import org.opencv.core.CvType
import org.opencv.core.Mat


/**
 * Created by cansik on 04.02.17.
 */
class ActiveRegionDetector {
    var threshold = 200.0
    var elementSize = 5
    var minAreaSize = 125

    init {

    }

    fun detect(depthImage: DepthImage) {
        val image = Mat(depthImage.input.height, depthImage.input.width, CvType.CV_8UC4)

        depthImage.input.toMat(image)
        val gray = image.copy()
        gray.gray()
        image.toBGRA(image)

        // threshold
        // todo: implement intelligent threshold
        gray.threshold(threshold)

        // remove small parts
        gray.erode(elementSize)
        gray.dilate(elementSize)

        // detect areas (used-component analysis)
        val nativeComponents = gray.connectedComponentsWithStats()
        val components = nativeComponents.getConnectedComponents().filter { it.area >= minAreaSize && it.label != 0 }

        depthImage.components.addAll(components)
        depthImage.gray = gray

        // free memory
        image.release()
        nativeComponents.release()
    }
}