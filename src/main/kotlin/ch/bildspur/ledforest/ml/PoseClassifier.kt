package ch.bildspur.ledforest.ml

import ch.bildspur.ledforest.pose.Pose
import smile.classification.KNN
import smile.classification.knn

class PoseClassifier() : BaseClassifier() {
    private var model: KNN<DoubleArray>? = null
    private val embedder = PoseEmbedder()

    private val samples = mutableMapOf<Int, MutableList<DoubleArray>>()

    val sampleCount: Int
        get() = samples.map { it.value.size }.sum()

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

        if (data.isEmpty()) {
            println("Could not fit knn because there are 0 samples.")
            return
        }

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
        if (model == null)
            return ClassificationResult(-1, 0.0f)

        val result = model!!.predict(embedding)
        return ClassificationResult(result, 0.0f)
    }

}