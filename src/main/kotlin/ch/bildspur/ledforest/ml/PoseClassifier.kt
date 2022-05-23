package ch.bildspur.ledforest.ml

import ch.bildspur.ledforest.pose.Pose
import smile.classification.knn

class PoseClassifier() : BaseClassifier() {
    private var model = knn(arrayOf(DoubleArray(0)), IntArray(0), 0)
    private val samples = mutableMapOf<Int, MutableList<DoubleArray>>()
    private val embedder = PoseEmbedder()

    override fun setup() {
        // todo: load saved embeddings
    }

    fun sample(pose: Pose, label: Int) {
        val embedding = embedder.create(pose)
        if (label !in samples) {
            samples[label] = mutableListOf()
        }
        samples[label]?.add(embedding)
    }

    fun fit() {
        val data = samples.flatMap { s -> s.value.map { s.key to it } }

        model = knn(
            data.map { it.second }.toTypedArray(),
            data.map { it.first }.toIntArray(),
            samples.keys.count()
        )
    }

    fun predict(pose: Pose): ClassificationResult {
        val embedding = embedder.create(pose)
        return predict(embedding)
    }

    override fun predict(embedding: DoubleArray): ClassificationResult {
        return ClassificationResult(model.predict(embedding), 0.0f)
    }

}