package com.example.pjm_match

enum class LessonStatus { DONE, CURRENT, LOCKED }

data class PathLesson(
    val title: String,
    val status: LessonStatus,
    val subtitle: String,
)

data class Topic(
    val title: String,
    val iconRes: Int,
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

data class Achievement(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val unlocked: Boolean,
    val warm: Boolean,
)

object Curriculum {
    const val XP_PER_ANSWER = 10

    val currentTopic = Topic(
        title = "Powitania",
        iconRes = R.drawable.ic_hand,
        lessonCount = 5,
        completedLessons = 2,
        availability = "Podstawy",
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
        Topic("Liczby", R.drawable.ic_hash, 4, 0, "możesz zacząć", true),
        Topic("Rodzina", R.drawable.ic_users, 5, 0, "po Powitaniach", false),
        Topic("Jedzenie", R.drawable.ic_utensils, 4, 0, "po Rodzinie", false),
        Topic("Codzienne zwroty", R.drawable.ic_messages, 6, 0, "po Jedzeniu", false),
    )

    val allTopics: List<Topic>
        get() = listOf(currentTopic) + nextTopics

    val totalLessons: Int
        get() = allTopics.sumOf { it.lessonCount }

    val completedLessons: Int
        get() = allTopics.sumOf { it.completedLessons }

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

    val achievements = listOf(
        Achievement("Pierwszy gest", "Pierwsza lekcja", R.drawable.ic_hand, unlocked = true, warm = false),
        Achievement("7 dni razem", "Tydzień nauki", R.drawable.ic_flame, unlocked = true, warm = true),
        Achievement("Mistrz liczb", "Jeszcze przed Tobą", R.drawable.ic_lock, unlocked = false, warm = false),
    )

    const val achievementsTotal = 6

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
    const val streakDays = 7
    const val totalXp = 240
    const val todayXp = 40
    const val studyMinutes = 38
    const val accuracyPercent = 92
    val weekXp = listOf(20, 30, 40, 20, 50, 40, 40)
    const val level = "Początkująca · PJM"
}
