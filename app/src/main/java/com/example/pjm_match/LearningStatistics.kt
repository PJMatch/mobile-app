package com.example.pjm_match

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/** In-memory statistics; replace the data source with account storage when it is available. */
object LearningStatistics {
    data class Technique(val trajectory: Float, val fluency: Float, val speed: Float, val orientation: Float, val fingers: Float) {
        init {
            require(listOf(trajectory, fluency, speed, orientation, fingers).all { it in 0f..1f })
        }
        fun values() = floatArrayOf(trajectory, fluency, speed, orientation, fingers)
    }

    private data class Attempt(val date: Date, val correct: Boolean, val xp: Int, val durationMillis: Long, val technique: Technique?)
    private val attempts = mutableListOf<Attempt>()
    private val completedLessons = mutableMapOf<String, String>()

    /** Call once for each evaluated attempt. Only a correct attempt awards XP. */
    @Synchronized
    fun recordAttempt(correct: Boolean, previousFailures: Int, durationMillis: Long, technique: Technique? = null, date: Date = Date()) {
        require(previousFailures >= 0 && durationMillis >= 0)
        attempts.add(Attempt(Date(date.time), correct, if (correct) SignExperience.reward(previousFailures) else 0, durationMillis, technique))
    }

    /** Lesson IDs must be stable and unique across topics. Repeating a lesson does not add another completion. */
    @Synchronized
    fun completeLesson(lessonId: String, topic: String) {
        require(lessonId.isNotBlank() && topic.isNotBlank())
        completedLessons.putIfAbsent(lessonId, topic)
    }

    private fun dayKey(date: Date) = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    private fun day(date: Date, offset: Int): Date = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, offset)
    }.time

    @Synchronized fun totalXp() = attempts.sumOf { it.xp }
    @Synchronized fun xpOn(date: Date): Int = attempts.filter { dayKey(it.date) == dayKey(date) }.sumOf { it.xp }
    @Synchronized fun completedCount() = completedLessons.size
    @Synchronized fun completedIn(topic: String) = completedLessons.values.count { it == topic }
    @Synchronized fun minutes() = attempts.sumOf { it.durationMillis } / 60_000
    @Synchronized fun accuracy(): Int? = if (attempts.isEmpty()) null else (100.0 * attempts.count { it.correct } / attempts.size).roundToInt()

    @Synchronized fun streak(today: Date = Date()): Int {
        val dates = attempts.map { dayKey(it.date) }.toSet()
        var cursor = if (dayKey(today) in dates) today else day(today, -1)
        var count = 0
        while (dayKey(cursor) in dates) {
            count++
            cursor = day(cursor, -1)
        }
        return count
    }

    @Synchronized fun technique(start: Date, end: Date): FloatArray? {
        val first = dayKey(start)
        val last = dayKey(end)
        val values = attempts.filter { dayKey(it.date) in first..last }.mapNotNull { it.technique?.values() }
        if (values.isEmpty()) return null
        return FloatArray(5) { index -> values.map { it[index].toDouble() }.average().toFloat() }
    }
}
