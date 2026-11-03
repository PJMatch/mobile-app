package com.example.pjm_match

class PjmSignRecording {

    companion object {
        const val TRAINING_FPS = 30
        private const val FRAME_INTERVAL_MS = 1000.0 / TRAINING_FPS
    }

    private val timestampsMs = ArrayList<Long>()
    private val frames = ArrayList<Array<FloatArray>>()

    @Synchronized
    fun add(timestampMs: Long, modelFrame: Array<FloatArray>) {
        if (timestampsMs.isNotEmpty() && timestampMs <= timestampsMs.last()) return
        timestampsMs.add(timestampMs)
        frames.add(modelFrame)
    }

    @Synchronized
    fun resampledToTrainingFps(): List<Array<FloatArray>> {
        if (frames.isEmpty()) return emptyList()
        val start = timestampsMs.first()
        val durationMs = timestampsMs.last() - start
        val frameCount = (durationMs / FRAME_INTERVAL_MS).toInt() + 1
        val resampled = ArrayList<Array<FloatArray>>(frameCount)
        var source = 0
        for (i in 0 until frameCount) {
            val targetMs = start + i * FRAME_INTERVAL_MS
            while (source + 1 < timestampsMs.size &&
                timestampsMs[source + 1] - targetMs <= targetMs - timestampsMs[source]
            ) {
                source++
            }
            resampled.add(frames[source])
        }
        return resampled
    }
}
