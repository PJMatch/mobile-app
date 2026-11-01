package com.example.pjm_match

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import org.json.JSONArray
import java.nio.FloatBuffer

data class SignPrediction(val gloss: String, val confidence: Float)

class PjmSignClassifier(context: Context, supportedGlosses: Collection<String>) : AutoCloseable {

    companion object {
        const val MIN_FRAMES = 8
        const val MAX_FRAMES = 900
        const val BLANK_GLOSS = "blank"
        private const val CHANNELS = 3
        private const val MODEL_ASSET = "pjm_islr.onnx"
        private const val LABELS_ASSET = "pjm_islr_labels.json"
    }

    private val environment = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val labels: List<String>
    private val allowedIndices: List<Int>

    init {
        val modelBytes = context.assets.open(MODEL_ASSET).use { it.readBytes() }
        session = environment.createSession(modelBytes, OrtSession.SessionOptions())
        val labelsJson = context.assets.open(LABELS_ASSET).bufferedReader().use { it.readText() }
        labels = JSONArray(labelsJson).let { array -> List(array.length()) { array.getString(it) } }
        val allowedGlosses = supportedGlosses.toSet() + BLANK_GLOSS
        allowedIndices = labels.indices.filter { labels[it] in allowedGlosses }
    }

    fun classify(clip: List<Array<FloatArray>>): SignPrediction {
        require(clip.size in MIN_FRAMES..MAX_FRAMES)
        val vertices = PjmLandmarks.TOTAL_V
        val input = FloatBuffer.allocate(CHANNELS * clip.size * vertices)
        for (channel in 0 until CHANNELS) {
            for (frame in clip) {
                for (vertex in 0 until vertices) {
                    input.put(frame[vertex][channel])
                }
            }
        }
        input.rewind()

        val shape = longArrayOf(1, CHANNELS.toLong(), clip.size.toLong(), vertices.toLong())
        OnnxTensor.createTensor(environment, input, shape).use { tensor ->
            session.run(mapOf(session.inputNames.first() to tensor)).use { result ->
                @Suppress("UNCHECKED_CAST")
                val probabilities = (result[0].value as Array<FloatArray>)[0]
                val best = allowedIndices.maxBy { probabilities[it] }
                val allowedTotal = allowedIndices.sumOf { probabilities[it].toDouble() }.toFloat()
                return SignPrediction(labels[best], probabilities[best] / allowedTotal)
            }
        }
    }

    override fun close() {
        session.close()
    }
}
