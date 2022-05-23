package ch.bildspur.ledforest.ml

abstract class BaseClassifier {
    abstract fun setup()

    abstract fun predict(embedding: DoubleArray): ClassificationResult
}