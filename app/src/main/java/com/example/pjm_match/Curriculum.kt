package com.example.pjm_match

import com.example.pjm_match.ui.IconSpec

enum class LessonStatus { DONE, CURRENT, LOCKED }

data class PathLesson(
    val title: String,
    val status: LessonStatus,
    val subtitle: String,
)

data class Topic(
    val title: String,
    val icon: IconSpec,
    val lessonCount: Int,
    val completedLessons: Int,
    val availability: String,
    val unlocked: Boolean,
)

data class LessonQuestion(
    val answer: String,
    val options: List<String>,
    val motion: String?,
    val explanation: String?,
    val videoAsset: String,
)

object Curriculum {
    const val XP_PER_ANSWER = 10

    val currentTopic = Topic(
        title = "Powitania",
        icon = Icons.pathHand,
        lessonCount = 5,
        completedLessons = 2,
        availability = "",
        unlocked = true,
    )

    val currentTopicLabel = "TEMAT 01 · PODSTAWY"

    val pathLessons = listOf(
        PathLesson("Pierwsze „cześć”", LessonStatus.DONE, "Ukończono · 3 znaki"),
        PathLesson("Dzień dobry", LessonStatus.DONE, "Ukończono · 4 znaki"),
        PathLesson("Uprzejme słowa", LessonStatus.CURRENT, "Lekcja 3 · 4 / 5 pytań"),
        PathLesson("Do zobaczenia", LessonStatus.LOCKED, "Ukończ poprzednią lekcję"),
        PathLesson("Mała rozmowa", LessonStatus.LOCKED, "Finał tematu"),
    )

    val currentLesson: PathLesson
        get() = pathLessons.first { it.status == LessonStatus.CURRENT }

    val currentLessonNumber: Int
        get() = pathLessons.indexOf(currentLesson) + 1

    val nextTopics = listOf(
        Topic("Liczby", Icons.pathHash, 4, 0, "możesz zacząć", true),
        Topic("Rodzina", Icons.pathUsers, 5, 0, "po Powitaniach", false),
        Topic("Jedzenie", Icons.pathUtensils, 4, 0, "po Rodzinie", false),
        Topic("Codzienne zwroty", Icons.pathMessages, 6, 0, "po Jedzeniu", false),
    )

    private val politeWords = listOf("Dzień dobry", "Cześć", "Dziękuję", "Do widzenia", "Proszę", "Przepraszam")

    val lessonQuestions = listOf(
        question("Proszę", listOf("Proszę", "Przepraszam", "Cześć", "Dziękuję")),
        question("Przepraszam", listOf("Do widzenia", "Przepraszam", "Proszę", "Dzień dobry")),
        question("Cześć", listOf("Cześć", "Dziękuję", "Do widzenia", "Przepraszam")),
        question(
            "Dziękuję",
            listOf("Dzień dobry", "Cześć", "Dziękuję", "Do widzenia"),
            motion = "Od brody → do przodu",
            explanation = "Dłoń odsuwa się od brody do przodu.",
        ),
        question("Do widzenia", listOf("Proszę", "Dzień dobry", "Przepraszam", "Do widzenia")),
    )

    private fun question(
        answer: String,
        options: List<String>,
        motion: String? = null,
        explanation: String? = null,
    ): LessonQuestion {
        require(answer in options && options.all { it in politeWords })
        return LessonQuestion(answer, options, motion, explanation, "signs/${slug(answer)}.mp4")
    }

    private fun slug(text: String): String {
        val map = mapOf('ą' to 'a', 'ć' to 'c', 'ę' to 'e', 'ł' to 'l', 'ń' to 'n', 'ó' to 'o', 'ś' to 's', 'ź' to 'z', 'ż' to 'z')
        return text.lowercase().map { map[it] ?: it }.joinToString("").replace(' ', '_')
    }
}

object LearnerStats {
    const val greetingName = "Aniu"
    const val initial = "A"
    const val streakDays = 7
    const val totalXp = 240
    const val todayXp = 40
    const val dailyGoalXp = 50
}
