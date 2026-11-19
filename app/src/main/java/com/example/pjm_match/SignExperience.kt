package com.example.pjm_match

import kotlin.math.pow
import kotlin.math.roundToInt

/** Calculates the reward once a sign has been performed correctly. */
object SignExperience {
    const val BASE_XP = 15.0
    private const val REMAINING_AFTER_ERROR = 0.9

    /** Each failure reduces the remaining reward by 10%; only the final reward is rounded. */
    fun reward(failedAttempts: Int): Int {
        require(failedAttempts >= 0) { "Failed attempts cannot be negative" }
        return (BASE_XP * REMAINING_AFTER_ERROR.pow(failedAttempts)).roundToInt()
    }
}
