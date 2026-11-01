package com.example.pjm_match

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.WeekBarsView
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.oval
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsActivity : BaseActivity() {

    override val tabRoot = Tab.STATS

    private val polish: Locale = Locale.forLanguageTag("pl")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(Tab.STATS) {
            place(eyebrow("Twój rozwój"))
            place(screenTitle("Widać postępy!"), top = 6)
            place(bodyText("Każdy gest ma znaczenie. Tak trzymaj, ${settings.name}."), top = 8)
            place(streakHero(), top = 24)
            place(statsGrid(), top = 20)
            place(weekCard(), top = 20)
            titleRow(this@StatsActivity, "Twoje tematy", "${Curriculum.completedLessons} / ${Curriculum.totalLessons} lekcje", top = 32)
            place(topicsCard())
            titleRow(this@StatsActivity, "Małe powody do dumy", "${Curriculum.achievements.count { it.unlocked }} / ${Curriculum.achievementsTotal}", top = 32)
            place(achievementsRow())
        }
    }

    private fun streakHero(): LinearLayout = horizontal {
        background = roundedRect(palette.hero, 28)
        pad(24, 24)
        place(vertical {
            place(label("Twoja aktualna seria", 15f, palette.heroSubtext, 500))
            place(label("${LearnerStats.streakDays} dni z rzędu", 34f, palette.onButton, 700), top = 6)
            place(label("Twój najlepszy wynik. Brawo!", 14f, palette.onButton, 400), top = 8)
        }, 0, WRAP, 1f)
        place(FrameLayout(context).apply {
            background = roundedRect(palette.heroIcon, 22)
            rotation = -8f
            place(icon(R.drawable.ic_flame, palette.accentStrong), 34, 34, gravity = Gravity.CENTER)
        }, 76, 76, start = 8)
    }

    private fun statsGrid(): LinearLayout = vertical {
        place(horizontal {
            place(statCard("${LearnerStats.totalXp} XP", "Łączne doświadczenie", R.drawable.ic_zap, palette.accent), 0, MATCH, 1f, end = 8)
            place(statCard("${Curriculum.completedLessons}", "Ukończone lekcje", R.drawable.ic_book_check, palette.textSecondary), 0, MATCH, 1f, start = 8)
        })
        place(horizontal {
            place(statCard("${LearnerStats.studyMinutes} min", "Czas nauki łącznie", R.drawable.ic_clock, palette.textSecondary), 0, MATCH, 1f, end = 8)
            place(statCard("${LearnerStats.accuracyPercent}%", "Poprawne odpowiedzi", R.drawable.ic_target, palette.accent), 0, MATCH, 1f, start = 8)
        }, top = 16)
    }

    private fun statCard(value: String, caption: String, iconRes: Int, iconColor: Int) = card(padding = 20) {
        place(horizontal {
            gravity = Gravity.TOP
            place(label(value, 30f, palette.primary, 700) { maxLines = 1 }, 0, WRAP, 1f)
            place(icon(iconRes, iconColor), 24, 24, top = 4)
        })
        place(label(caption, 14.5f, palette.textSecondary), top = 10)
    }

    private fun weekCard(): LinearLayout = card(padding = 22) {
        val week = currentWeek()
        place(horizontal {
            gravity = Gravity.TOP
            place(vertical {
                place(label("Twój tydzień", 21f, palette.primary, 700))
                place(label(week.first, 14f, palette.textSecondary), top = 4)
            }, 0, WRAP, 1f)
            place(label("${LearnerStats.weekXp.sum()} XP", 19f, palette.accent, 700), WRAP, WRAP, top = 6)
        })
        place(
            WeekBarsView(context, palette, LearnerStats.weekXp, listOf("Pn", "Wt", "Śr", "Cz", "Pt", "So", "Nd"), week.second),
            MATCH, WRAP, top = 18,
        )
    }

    private fun currentWeek(): Pair<String, Int> {
        val calendar = Calendar.getInstance(polish)
        val todayIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
        calendar.add(Calendar.DAY_OF_MONTH, -todayIndex)
        val start = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val end = calendar.time
        val day = SimpleDateFormat("d", polish)
        val full = SimpleDateFormat("d MMMM", polish)
        val sameMonth = SimpleDateFormat("M", polish).let { it.format(start) == it.format(end) }
        val range = if (sameMonth) "${day.format(start)}–${full.format(end)}" else "${full.format(start)} – ${full.format(end)}"
        return range to todayIndex
    }

    private fun topicsCard(): LinearLayout = card(padding = 22) {
        Curriculum.allTopics.forEachIndexed { i, topic ->
            place(horizontal {
                place(label(topic.title, 16.5f, palette.text, 500), 0, WRAP, 1f)
                place(label("${topic.completedLessons} / ${topic.lessonCount}", 15f, palette.text, 600), WRAP, WRAP)
            }, top = if (i == 0) 0 else 20)
            place(progressBar(topic.completedLessons.toFloat() / topic.lessonCount), MATCH, 6, top = 10)
        }
    }

    private fun achievementsRow(): LinearLayout = horizontal {
        gravity = Gravity.TOP
        Curriculum.achievements.forEach { achievement ->
            place(vertical {
                gravity = Gravity.CENTER_HORIZONTAL
                val background = when {
                    !achievement.unlocked -> palette.locked
                    achievement.warm -> palette.accentTint
                    else -> palette.tint
                }
                val iconColor = when {
                    !achievement.unlocked -> palette.textSecondary
                    achievement.warm -> palette.accent
                    else -> palette.primary
                }
                place(FrameLayout(context).apply {
                    this.background = oval(background, palette.surface, 4)
                    place(icon(achievement.iconRes, iconColor), 30, 30, gravity = Gravity.CENTER)
                }, 76, 76)
                place(label(achievement.title, 15f, if (achievement.unlocked) palette.primary else palette.textSecondary, 700) {
                    gravity = Gravity.CENTER
                }, WRAP, WRAP, top = 12)
                place(label(achievement.subtitle, 12.5f, palette.textSecondary) { gravity = Gravity.CENTER }, WRAP, WRAP, top = 4)
            }, 0, WRAP, 1f)
        }
    }
}
