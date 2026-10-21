package com.example.pjm_match

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker

class PjmSkeleton(
    val timestampMs: Long,
    val pose: List<NormalizedLandmark>,
    val face: List<NormalizedLandmark>,
    val leftHand: List<NormalizedLandmark>,
    val rightHand: List<NormalizedLandmark>,
) {
    fun toModelFrame(): Array<FloatArray> = PjmLandmarks.buildFrame(pose, face, leftHand, rightHand)
}

class PjmLandmarkExtractor(
    context: Context,
    private val onSkeleton: (PjmSkeleton) -> Unit,
) : AutoCloseable {

    private class PartialSkeleton {
        var pose: List<NormalizedLandmark>? = null
        var face: List<NormalizedLandmark>? = null
        var hands: Pair<List<NormalizedLandmark>, List<NormalizedLandmark>>? = null
    }

    private val pending = HashMap<Long, PartialSkeleton>()
    private var lastTimestampMs = 0L

    private val poseLandmarker = PoseLandmarker.createFromOptions(
        context,
        PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(modelAsset("pose_landmarker_lite.task"))
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                update(result.timestampMs()) { it.pose = result.landmarks().firstOrNull().orEmpty() }
            }
            .setErrorListener { it.printStackTrace() }
            .build()
    )

    private val handLandmarker = HandLandmarker.createFromOptions(
        context,
        HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(modelAsset("hand_landmarker.task"))
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumHands(2)
            .setResultListener { result, _ ->
                var left: List<NormalizedLandmark> = emptyList()
                var right: List<NormalizedLandmark> = emptyList()
                result.landmarks().forEachIndexed { index, hand ->
                    when (result.handedness().getOrNull(index)?.firstOrNull()?.categoryName()) {
                        "Left" -> left = hand
                        "Right" -> right = hand
                    }
                }
                update(result.timestampMs()) { it.hands = left to right }
            }
            .setErrorListener { it.printStackTrace() }
            .build()
    )

    private val faceLandmarker = FaceLandmarker.createFromOptions(
        context,
        FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(modelAsset("face_landmarker.task"))
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumFaces(1)
            .setResultListener { result, _ ->
                update(result.timestampMs()) { it.face = result.faceLandmarks().firstOrNull().orEmpty() }
            }
            .setErrorListener { it.printStackTrace() }
            .build()
    )

    fun detectAsync(uprightUnmirroredFrame: Bitmap, timestampMs: Long) {
        val monotonicTimestampMs = maxOf(timestampMs, lastTimestampMs + 1)
        lastTimestampMs = monotonicTimestampMs
        val image = BitmapImageBuilder(uprightUnmirroredFrame).build()
        poseLandmarker.detectAsync(image, monotonicTimestampMs)
        handLandmarker.detectAsync(image, monotonicTimestampMs)
        faceLandmarker.detectAsync(image, monotonicTimestampMs)
    }

    private fun update(timestampMs: Long, apply: (PartialSkeleton) -> Unit) {
        val skeleton = synchronized(pending) {
            val partial = pending.getOrPut(timestampMs) { PartialSkeleton() }
            apply(partial)
            val pose = partial.pose ?: return
            val face = partial.face ?: return
            val hands = partial.hands ?: return
            pending.keys.removeAll { it <= timestampMs }
            PjmSkeleton(timestampMs, pose, face, hands.first, hands.second)
        }
        onSkeleton(skeleton)
    }

    override fun close() {
        poseLandmarker.close()
        handLandmarker.close()
        faceLandmarker.close()
    }

    private fun modelAsset(path: String) = BaseOptions.builder().setModelAssetPath(path).build()
}
