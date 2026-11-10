package com.example.pjm_match

import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import com.example.pjm_match.ui.Fonts
import com.example.pjm_match.ui.IconSpec
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.ProgressBarView
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical
import com.example.pjm_match.ui.withAlpha
import java.io.File

class LessonActivity : BaseActivity() {

    private val questions = Curriculum.lessonQuestions
    private var index = 0
    private var earnedXp = 0
    private var answered = false

    private lateinit var progress: ProgressBarView
    private lateinit var counter: TextView
    private lateinit var videoView: VideoView
    private lateinit var videoPlaceholder: LinearLayout
    private lateinit var motionTitle: TextView
    private lateinit var motionTime: TextView
    private lateinit var optionsGrid: LinearLayout
    private lateinit var feedback: LinearLayout
    private lateinit var nextButton: ActionButton
    private lateinit var footerNote: TextView
    private val optionViews = mutableListOf<Pair<String, LinearLayout>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(
            navTab = null,
            footer = {
                nextButton = place(primaryButton("Dalej", Icons.lessonArrowRight, iconFirst = true) { next() }, MATCH, 56)
                footerNote = place(label("", 13.5f, Palette.textSecondary) { gravity = Gravity.CENTER }, top = 10)
            },
        ) {
            place(header())
            val topic = Curriculum.currentTopic.title
            place(eyebrow("$topic · ${Curriculum.currentLesson.title}"), top = 28)
            place(label("Co oznacza ten znak?", 30f, Palette.text, 800), top = 8)
            place(bodyText("Obejrzyj ruch dłoni i wybierz odpowiedź."), top = 8)
            place(videoCard(), MATCH, 280, top = 22)
            optionsGrid = place(vertical(), top = 20)
            feedback = place(vertical(), top = 16)
        }
        showQuestion()
    }

    private fun header(): LinearLayout = horizontal {
        place(icon(Icons.lessonClose, Palette.textSecondary).apply {
            pad(4)
            contentDescription = "Zamknij lekcję"
            onTap(20, Palette.ripple) { finish() }
        }, 34, 34, start = -4)
        progress = place(ProgressBarView(context, Palette.accentStrong, Palette.tint), 0, 10, 1f, start = 16, end = 16)
        counter = place(label("", 16f, Palette.primary, 700), WRAP, WRAP)
    }

    private fun videoCard(): FrameLayout = FrameLayout(this).apply {
        background = roundedRect(Palette.tint, 28, Palette.border, 1)
        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true

        videoView = place(VideoView(context), MATCH, MATCH, gravity = Gravity.CENTER)
        videoPlaceholder = place(vertical {
            gravity = Gravity.CENTER
            place(icon(Icons.lessonPlaceholderHand, Palette.primary.withAlpha(0.6f)), WRAP, WRAP)
            place(label("Nagranie znaku pojawi się wkrótce", 14f, Palette.textSecondary, 500), WRAP, WRAP, top = 10)
        }, MATCH, MATCH)

        place(pill("PJM · widok z przodu", Palette.text, Palette.surface, Icons.lessonVideo, 13f), WRAP, WRAP, top = 14, start = 14)

        place(horizontal {
            background = roundedRect(Palette.surface.withAlpha(0.94f), 16)
            pad(14, 10)
            place(icon(Icons.lessonPlay, Palette.primary).apply {
                contentDescription = "Odtwórz"
                onTap(16, Palette.ripple) { playVideo() }
            }, 26, 26, end = 12)
            place(vertical {
                motionTitle = place(label("", 15f, Palette.text, 700) { maxLines = 1 })
                motionTime = place(label("", 12.5f, Palette.textSecondary), top = 2)
            }, 0, WRAP, 1f)
            place(icon(Icons.lessonReplay, Palette.primary).apply {
                pad(3)
                contentDescription = "Odtwórz ponownie"
                onTap(16, Palette.ripple) { playVideo() }
            }, 28, 28, start = 8)
        }, MATCH, WRAP, start = 12, end = 12, bottom = 12, gravity = Gravity.BOTTOM)
    }

    private fun showQuestion() {
        val question = questions[index]
        answered = false
        progress.fraction = index.toFloat() / questions.size
        counter.text = "${index + 1} / ${questions.size}"
        motionTitle.text = question.motion ?: "Obejrzyj ruch dłoni"
        loadVideo(question)

        optionsGrid.removeAllViews()
        optionViews.clear()
        question.options.chunked(2).forEachIndexed { rowIndex, pair ->
            optionsGrid.place(horizontal {
                pair.forEachIndexed { i, option ->
                    val view = optionCard(option)
                    optionViews += option to view
                    place(view, 0, 72, 1f, start = if (i == 1) 6 else 0, end = if (i == 0) 6 else 0)
                }
            }, top = if (rowIndex == 0) 0 else 12)
        }

        feedback.removeAllViews()
        feedback.visibility = View.GONE
        nextButton.isEnabled = false
        nextButton.setContent(if (index == questions.lastIndex) "Zakończ lekcję" else "Dalej", Icons.lessonArrowRight, iconFirst = true)
        footerNote.text = "Wybierz jedną z odpowiedzi"
    }

    private fun optionCard(option: String): LinearLayout = horizontal {
        background = roundedRect(Palette.surface, 18, Palette.border, 1)
        pad(18, 0)
        place(label(option, 17f, Palette.text, 500) { maxLines = 2 }, 0, WRAP, 1f)
        onTap(18, Palette.ripple) { answer(option) }
    }

    private fun answer(option: String) {
        if (answered) return
        answered = true
        val question = questions[index]
        val correct = option == question.answer
        if (correct) earnedXp += Curriculum.XP_PER_ANSWER

        optionViews.forEach { (text, view) ->
            val isAnswer = text == question.answer
            val isChosen = text == option
            when {
                isAnswer -> markOption(view, Palette.tint, Palette.primary, Icons.lessonOptionCheck, Palette.primary)
                isChosen -> markOption(view, Palette.accentTint, Palette.accent, Icons.lessonClose, Palette.accent)
                else -> view.alpha = 0.6f
            }
            view.isClickable = false
        }

        showFeedback(question, correct)
        nextButton.isEnabled = true
        val goal = LearnerStats.dailyGoalXp
        footerNote.text = if (correct) {
            "XP dodane! Twój dzienny cel: ${minOf(LearnerStats.todayXp + earnedXp, goal)} / $goal XP"
        } else {
            "Następnym razem się uda. Ćwiczenie czyni mistrza."
        }
    }

    private fun markOption(view: LinearLayout, background: Int, border: Int, icon: IconSpec, iconColor: Int) {
        view.background = roundedRect(background, 18, border, 2)
        (view.getChildAt(0) as TextView).typeface = Fonts.weight(700)
        view.place(icon(icon, iconColor), WRAP, WRAP, start = 8)
    }

    private fun showFeedback(question: LessonQuestion, correct: Boolean) {
        feedback.removeAllViews()
        feedback.background = roundedRect(if (correct) Palette.tint else Palette.accentTint, 24)
        feedback.pad(22, 20)
        feedback.place(horizontal {
            place(icon(if (correct) Icons.lessonFeedbackCheck else Icons.trInfo, if (correct) Palette.primary else Palette.accent), WRAP, WRAP, end = 12)
            place(label(if (correct) "Dokładnie tak!" else "Prawie!", 23f, Palette.text, 700), 0, WRAP, 1f)
            if (correct) place(pill("+${Curriculum.XP_PER_ANSWER} XP", Palette.accent, Palette.accentTint, Icons.lessonZap), WRAP, WRAP)
        })
        val explanation = buildString {
            append(if (correct) "Ten znak oznacza „${question.answer}”." else "Ten znak oznacza „${question.answer}”. Obejrzyj nagranie jeszcze raz.")
            question.explanation?.let { append(' ').append(it) }
        }
        feedback.place(label(explanation, 16f, Palette.textSecondary), top = 14)
        feedback.visibility = View.VISIBLE
    }

    private fun next() {
        if (!answered) return
        if (index == questions.lastIndex) {
            toast("Lekcja ukończona! Zdobyte XP: $earnedXp", long = true)
            finish()
            return
        }
        index++
        showQuestion()
    }

    private fun loadVideo(question: LessonQuestion) {
        val file = assetVideo(question.videoAsset)
        videoView.stopPlayback()
        if (file == null) {
            videoView.visibility = View.INVISIBLE
            videoPlaceholder.visibility = View.VISIBLE
            motionTime.text = "Nagranie niedostępne"
            return
        }
        videoPlaceholder.visibility = View.GONE
        videoView.visibility = View.VISIBLE
        videoView.setVideoPath(file.absolutePath)
        videoView.setOnPreparedListener { player: MediaPlayer ->
            player.isLooping = false
            motionTime.text = "00:00 / ${formatDuration(player.duration)} · odtwórz ponownie"
            videoView.start()
        }
    }

    private fun playVideo() {
        if (videoView.visibility != View.VISIBLE) {
            toast("Nagranie tego znaku nie jest jeszcze dostępne")
            return
        }
        videoView.seekTo(0)
        videoView.start()
    }

    private fun assetVideo(path: String): File? {
        val name = path.substringAfterLast('/')
        val directory = path.substringBeforeLast('/', "")
        if (assets.list(directory)?.contains(name) != true) return null
        val target = File(cacheDir, path)
        if (!target.exists()) {
            target.parentFile?.mkdirs()
            assets.open(path).use { input -> target.outputStream().use { input.copyTo(it) } }
        }
        return target
    }

    private fun formatDuration(ms: Int): String {
        val seconds = ms / 1000
        return "%02d:%02d".format(seconds / 60, seconds % 60)
    }

    override fun onPause() {
        super.onPause()
        if (::videoView.isInitialized) videoView.pause()
    }
}
