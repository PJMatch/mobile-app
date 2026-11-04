package com.example.pjm_match

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import com.example.pjm_match.ui.LearningPathLayout
import com.example.pjm_match.ui.LearningPathLayout.Side
import com.example.pjm_match.ui.LearningPathLayout.Slot
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.iconTile
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.oval
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical

class LearningPathActivity : BaseActivity() {

    override val tabRoot = Tab.LEARN

    private val slots = listOf(
        Slot(0.22f, 34, 42, Side.RIGHT, 0.33f),
        Slot(0.60f, 108, 42, Side.LEFT, 0.06f),
        Slot(0.37f, 196, 64, Side.RIGHT, 0.52f),
        Slot(0.71f, 286, 40, Side.LEFT, 0.12f),
        Slot(0.42f, 348, 40, Side.RIGHT, 0.55f),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(Tab.LEARN) {
            place(horizontal {
                gravity = Gravity.TOP
                place(vertical {
                    place(eyebrow("Polski język migowy", Palette.primary))
                    place(screenTitle("Gest po geście"), top = 6)
                }, 0, WRAP, 1f)
                place(pill("${LearnerStats.streakDays} dni", Palette.accent, Palette.accentTint, Icons.pathFlame), WRAP, WRAP, top = 18)
            })
            place(topicCard(), top = 24)
            place(path(), top = 24)
            place(sectionTitle("Odkrywaj dalej"), top = 24, bottom = 14)
            Curriculum.nextTopics.forEach { place(topicRow(it), bottom = 12) }
        }
    }

    private fun topicCard(): LinearLayout = card(background = Palette.tint, border = Palette.tint) {
        val topic = Curriculum.currentTopic
        place(horizontal {
            place(icon(topic.icon, Palette.primary), WRAP, WRAP, end = 16)
            place(vertical {
                place(eyebrow(Curriculum.currentTopicLabel, Palette.primary))
                place(label(topic.title, 28f, Palette.text, 800), top = 2)
            }, 0, WRAP, 1f)
            place(label("${topic.completedLessons} / ${topic.lessonCount}", 17f, Palette.primary, 700), WRAP, WRAP)
        })
        place(progressBar(topic.completedLessons.toFloat() / topic.lessonCount, track = Palette.surface), MATCH, 10, top = 18)
    }

    private fun path(): LearningPathLayout = LearningPathLayout(this).apply {
        Curriculum.pathLessons.forEachIndexed { index, lesson ->
            addStep(slots[index], pathNode(lesson), pathLabel(lesson), lesson.status != LessonStatus.LOCKED)
        }
    }

    private fun pathNode(lesson: PathLesson): View = FrameLayout(this).apply {
        contentDescription = lesson.title
        when (lesson.status) {
            LessonStatus.DONE -> {
                background = oval(Palette.primary)
                place(icon(Icons.pathCheck, Palette.onPrimary), WRAP, WRAP, gravity = Gravity.CENTER)
                onTap(100, Palette.ripple) { toast("Lekcja „${lesson.title}” jest już ukończona") }
            }
            LessonStatus.CURRENT -> {
                background = oval(Palette.tint, Palette.primary, 2)
                place(icon(Icons.pathPlay, Palette.primary), WRAP, WRAP, gravity = Gravity.CENTER, start = 3)
                onTap(100, Palette.ripple) { openLesson() }
            }
            LessonStatus.LOCKED -> {
                background = oval(Palette.locked)
                place(icon(Icons.pathLock, Palette.textSecondary), WRAP, WRAP, gravity = Gravity.CENTER)
                onTap(100, Palette.ripple) { toast("Ukończ najpierw poprzednie lekcje") }
            }
        }
    }

    private fun pathLabel(lesson: PathLesson): View = vertical {
        val locked = lesson.status == LessonStatus.LOCKED
        val current = lesson.status == LessonStatus.CURRENT
        val titleColor = when {
            locked -> Palette.textSecondary
            current -> Palette.primary
            else -> Palette.text
        }
        place(label(lesson.title, 18f, titleColor, 600), WRAP, WRAP)
        val subtitleColor = if (lesson.status == LessonStatus.DONE) Palette.primary else Palette.textSecondary
        place(label(lesson.subtitle, 13.5f, subtitleColor, 700), WRAP, WRAP, top = 4)
        if (current) {
            place(label("Kontynuuj →", 14f, Palette.primary, 700) {
                pad(0, 4)
                onTap(8, Palette.ripple) { openLesson() }
            }, WRAP, WRAP, top = 4)
        }
    }

    private fun topicRow(topic: Topic): LinearLayout = horizontal {
        background = roundedRect(Palette.surface, 20, Palette.border, 1)
        pad(18, 16)
        place(iconTile(topic.icon, Palette.primary, Palette.tint, radiusDp = 14), 52, 52)
        place(vertical {
            place(label(topic.title, 19f, Palette.text, 700))
            place(label("${topic.completedLessons} / ${topic.lessonCount} lekcji · ${topic.availability}", 13.5f, Palette.textSecondary, 600), top = 3)
        }, 0, WRAP, 1f, start = 16, end = 12)
        val trailing = if (topic.unlocked) icon(Icons.pathArrowRight, Palette.primary) else icon(Icons.pathLockSmall, Palette.textSecondary)
        place(trailing, WRAP, WRAP)
        onTap(20, Palette.ripple) {
            toast(if (topic.unlocked) "Temat „${topic.title}” pojawi się wkrótce" else "Ten temat odblokujesz ${topic.availability}")
        }
    }

    private fun openLesson() {
        val intent = Intent(this, SignActivity::class.java).apply {
            putExtra(SignActivity.EXTRA_SIGN, "Dzień dobry")
        }
        startActivity(intent)
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
