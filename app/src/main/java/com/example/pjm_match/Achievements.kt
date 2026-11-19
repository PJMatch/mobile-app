package com.example.pjm_match

import com.example.pjm_match.ui.IconSpec

data class Achievement(
    val id: String,
    val title: String,
    val requirement: String,
    val icon: IconSpec,
    val isUnlocked: Boolean = false,
)

object Achievements {
    // Temporary presentation data. Connect statuses to saved learning progress later.
    val all = listOf(
        Achievement("first_lesson", "Pierwszy krok", "Ukończ pierwszą lekcję.", Icons.pathHand),
        Achievement("seven_day_streak", "7 dni razem", "Ucz się przez 7 kolejnych dni.", Icons.startFlame),
        Achievement("numbers", "Mistrz liczb", "Ukończ wszystkie lekcje z liczb.", Icons.pathHash),
        Achievement("perfect_lesson", "Bez pomyłki", "Ukończ lekcję ze wszystkimi poprawnymi odpowiedziami.", Icons.lessonFeedbackCheck),
        Achievement("daily_goal", "Mały cel, wielki krok", "Osiągnij dzienny cel XP pierwszy raz.", Icons.startTarget),
        Achievement("greetings", "Znam powitania", "Ukończ wszystkie lekcje z powitań.", Icons.pathMessages),
        Achievement("ten_signs", "Kolekcjoner znaków", "Poprawnie rozpoznaj 10 różnych znaków.", Icons.trSparkles),
        Achievement("ten_lessons", "Wytrwały uczeń", "Ukończ 10 różnych lekcji.", Icons.profileBook),
    )

    val unlockedCount: Int
        get() = all.count { it.isUnlocked }

    val countLabel: String
        get() = "$unlockedCount / ${all.size}"
}
