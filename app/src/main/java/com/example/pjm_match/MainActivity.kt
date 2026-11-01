package com.example.pjm_match

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.dp
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.iconTile
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.spacer
import com.example.pjm_match.ui.vertical
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity() {

    override val tabRoot = Tab.START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        render()
    }

    override fun onRestart() {
        super.onRestart()
        render()
    }

    private fun render() {
        scaffold(Tab.START) {
            place(header())
            place(statsRow(), top = 24)
            place(dailyGoalCard(), top = 16)
            place(sectionTitle("Czas na kolejny gest"), top = 32, bottom = 14)
            place(lessonHero())
            place(shortcutsRow(), top = 20)
        }
    }

    private fun header(): LinearLayout = horizontal {
        gravity = Gravity.TOP
        val polish = Locale.forLanguageTag("pl")
        val date = SimpleDateFormat("EEEE, d MMMM", polish).format(Date()).uppercase(polish)
        place(vertical {
            place(label(date, 12.5f, palette.textSecondary, 700) { letterSpacing = 0.04f })
            place(screenTitle("Cześć, ${settings.name}!"), top = 6)
        }, 0, WRAP, 1f)
        place(avatar(44, 20f, ring = true).apply {
            contentDescription = "Profil"
            onTap(22, palette.ripple) { openTab(Tab.PROFILE) }
        }, 44, 44, top = 8)
    }

    private fun statsRow(): LinearLayout = horizontal {
        place(statTile(R.drawable.ic_flame, palette.accent, palette.accentTint, "${LearnerStats.streakDays} dni", "Twoja seria", palette.accent), 0, 88, 1f, end = 8)
        place(statTile(R.drawable.ic_zap, palette.primary, palette.tint, "${LearnerStats.totalXp} XP", "Łącznie", palette.textSecondary), 0, 88, 1f, start = 8)
    }

    private fun statTile(iconRes: Int, iconColor: Int, background: Int, value: String, caption: String, captionColor: Int) = horizontal {
        this.background = roundedRect(background, 20)
        pad(18, 0)
        place(icon(iconRes, iconColor), 26, 26, end = 14)
        place(vertical {
            place(label(value, 24f, palette.text, 700) { maxLines = 1 })
            place(label(caption, 14f, captionColor, 500), top = 2)
        }, 0, WRAP, 1f)
    }

    private fun dailyGoalCard(): LinearLayout = card {
        val goal = settings.dailyGoalXp
        val today = LearnerStats.todayXp
        place(horizontal {
            place(icon(R.drawable.ic_target, palette.text), 24, 24, end = 12)
            place(label("Twój dzienny cel", 18f, palette.text, 700), 0, WRAP, 1f)
            place(label("$today / $goal XP", 16f, palette.primary, 700), WRAP, WRAP)
        })
        place(progressBar(today.toFloat() / goal), MATCH, 10, top = 16)
        val remaining = goal - today
        val message = if (remaining > 0) "Jeszcze $remaining XP. Jeden mały krok i cel osiągnięty!" else "Cel na dziś osiągnięty. Świetna robota!"
        place(label(message, 14.5f, palette.textSecondary), top = 14)
    }

    private fun lessonHero(): LinearLayout = vertical {
        background = roundedRect(palette.hero, 28)
        pad(22, 24)
        val lesson = Curriculum.currentLesson
        val topic = Curriculum.currentTopic
        val answered = Curriculum.lessonQuestions.size - 1
        place(horizontal {
            gravity = Gravity.TOP
            place(vertical {
                place(label("TWOJA ŚCIEŻKA · PJM", 12f, palette.accentStrong, 700) { letterSpacing = 0.04f })
                place(label("Ucz się", 32f, palette.onButton, 700), top = 8)
                place(label("${topic.title} · Lekcja ${Curriculum.currentLessonNumber} z ${topic.lessonCount}", 15f, palette.heroSubtext), top = 6)
            }, 0, WRAP, 1f)
            place(FrameLayout(context).apply {
                background = roundedRect(palette.heroIcon, 22)
                rotation = -8f
                place(icon(R.drawable.ic_hand, palette.accentStrong), 34, 34, gravity = Gravity.CENTER)
            }, 68, 68, top = 4, end = 4)
        })
        place(horizontal {
            place(label(lesson.title, 15f, palette.onButton, 500), 0, WRAP, 1f)
            place(label("$answered / ${Curriculum.lessonQuestions.size} pytań", 15f, palette.heroSubtext, 700), WRAP, WRAP)
        }, top = 22)
        place(progressBar(answered.toFloat() / Curriculum.lessonQuestions.size, palette.accentStrong, palette.heroTrack), MATCH, 6, top = 10)
        place(ActionButton(context, palette.heroButtonText).apply {
            background = roundedRect(palette.heroButton, 18)
            setContent("Kontynuuj lekcję", R.drawable.ic_arrow_right)
            onTap(18, palette.ripple) { startActivity(Intent(this@MainActivity, LessonActivity::class.java)) }
        }, MATCH, 52, top = 22)
    }

    private fun shortcutsRow(): LinearLayout = horizontal {
        place(
            shortcutCard(R.drawable.ic_scan, palette.primary, palette.tint, "Tłumacz miganie", "Pokaż znak. Poznaj jego znaczenie z AI.") { openTab(Tab.TRANSLATOR) },
            0, MATCH, 1f, end = 8,
        )
        place(
            shortcutCard(R.drawable.ic_chart, palette.accent, palette.accentTint, "Statystyki", "Zobacz, jak rosną Twoje umiejętności.") { openTab(Tab.STATS) },
            0, MATCH, 1f, start = 8,
        )
    }

    private fun shortcutCard(iconRes: Int, color: Int, background: Int, title: String, description: String, onClick: () -> Unit) =
        card(padding = 18) {
            minimumHeight = context.dp(184)
            place(iconTile(iconRes, color, background, iconSizeDp = 24, radiusDp = 14), 46, 46)
            place(label(title, 19f, palette.text, 700), top = 18)
            place(label(description, 14.5f, palette.textSecondary), top = 6)
            spacer()
            place(icon(R.drawable.ic_arrow_up_right, color), 18, 18, top = 14)
            onTap(24, palette.ripple, onClick)
        }
}
