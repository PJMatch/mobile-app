package com.example.pjm_match

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
// From src/stgcn/config.json in PJM-to-text repo
object PjmLandmarks {
    const val POSE_LEN = 33
    const val FACE_LEN = 478
    const val LH_LEN = 21
    const val RH_LEN = 21
    const val TOTAL_V = POSE_LEN + FACE_LEN + LH_LEN + RH_LEN
    const val BODY_OFFSET = 0
    const val FACE_OFFSET = POSE_LEN
    const val LH_OFFSET = POSE_LEN + FACE_LEN
    const val RH_OFFSET = LH_OFFSET + LH_LEN

    val FACE = intArrayOf(1, 33, 133, 159, 145, 362, 263, 386, 374, 70, 63, 105, 66, 336, 296, 334, 293, 300)
    val MOUTH = intArrayOf(0, 17, 61, 291, 37, 267, 84, 314)
    val HAND = IntArray(21) { it }
    val BODY = intArrayOf(0, 11, 12, 13, 14, 15, 16, 23, 24)

    fun buildFrame(
        pose: List<NormalizedLandmark>?,
        face: List<NormalizedLandmark>?,
        leftHand: List<NormalizedLandmark>?,
        rightHand: List<NormalizedLandmark>?,
    ): Array<FloatArray> {
        val frame = Array(TOTAL_V) { FloatArray(3) }
        fun fill(points: List<NormalizedLandmark>?, offset: Int, len: Int, usePoseVisibility: Boolean) {
            points ?: return
            for (i in 0 until minOf(points.size, len)) {
                val lm = points[i]
                frame[offset + i][0] = lm.x()
                frame[offset + i][1] = lm.y()
                frame[offset + i][2] = if (usePoseVisibility) lm.visibility().orElse(1f) else 1f
            }
        }
        fill(pose, BODY_OFFSET, POSE_LEN, true)
        fill(face, FACE_OFFSET, FACE_LEN, false)
        fill(leftHand, LH_OFFSET, LH_LEN, false)
        fill(rightHand, RH_OFFSET, RH_LEN, false)
        return frame
    }
}
