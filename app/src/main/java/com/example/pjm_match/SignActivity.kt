package com.example.pjm_match

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.pjm_match.ui.Palette

class SignActivity : BaseActivity() {

    private lateinit var sign: String
    private lateinit var lastScoreValue: TextView
    private lateinit var bestScoreValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        sign = intent.getStringExtra(EXTRA_SIGN) ?: "Dzień dobry"

        val openFullResults = {
            if (SignPracticeStore.progress(this, sign) == null) {
                toast("Nagraj znak, aby zobaczyć wyniki")
            } else {
                startActivity(
                    Intent(this@SignActivity, SignResultsActivity::class.java)
                        .putExtra(SignResultsActivity.EXTRA_SIGN_NAME, sign)
                )
            }
        }

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Palette.background)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 48, 40, 40)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // 1. Top Bar Navigation
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 24)
        }
        
        val backBtn = TextView(this).apply {
            text = "Wróć"
            textSize = 16f
            setTextColor(Palette.primary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 32, 0)
            setOnClickListener { finish() }
        }
        topBar.addView(backBtn)
        rootLayout.addView(topBar)

        // 2. Header Title
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 32)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val stepTextView = TextView(this).apply {
            text = "Krok 1 / 2: Naucz się znaku"
            textSize = 12f
            setTextColor(Palette.accent)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 6)
        }
        
        val signNameTextView = TextView(this).apply {
            text = sign
            textSize = 30f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        headerLayout.addView(stepTextView)
        headerLayout.addView(signNameTextView)
        rootLayout.addView(headerLayout)
        
        // 3. Video Instructions Card
        val videoContainer = FrameLayout(this).apply {
            val bgDrawable = GradientDrawable().apply {
                setColor(Palette.surface)
                setStroke(2, Palette.border)
                cornerRadius = 40f
            }
            background = bgDrawable
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                480
            ).apply { bottomMargin = 32 }
        }

        val placeholderIcon = TextView(this).apply {
            text = "Odtwórz"
            textSize = 20f
            setTextColor(Palette.primary)
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ).apply { topMargin = -30 }
        }
        
        val placeholderText = TextView(this).apply {
            text = "Odtwarzacz instruktażu\n(Wideo z gestem PJM)"
            textSize = 14f
            setTextColor(Palette.textSecondary)
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ).apply { topMargin = 60 }
        }

        videoContainer.addView(placeholderIcon)
        videoContainer.addView(placeholderText)
        rootLayout.addView(videoContainer)

        // 4. Quality Metrics Preview Section
        val metricsHeader = TextView(this).apply {
            text = "Twój Ostatni Wynik i Jakość Gestu:"
            textSize = 18f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }
        rootLayout.addView(metricsHeader)

        // Row of Metric Summary Cards
        val metricsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 28 }
        }

        lastScoreValue = metricValueView()
        val cardLast = createClickableMetricCard("Ostatnia próba", lastScoreValue, Palette.accent, openFullResults)
        cardLast.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = 12 }

        bestScoreValue = metricValueView()
        val cardBest = createClickableMetricCard("Najlepszy wynik", bestScoreValue, Palette.primary, openFullResults)
        cardBest.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        metricsRow.addView(cardLast)
        metricsRow.addView(cardBest)
        rootLayout.addView(metricsRow)

        // 5. Main Action Buttons
        val startPracticeButton = Button(this).apply {
            text = "Nagraj znak (Rozpocznij ćwiczenie)"
            textSize = 15f
            setTextColor(Palette.onPrimary)
            isAllCaps = false
            val btnBg = GradientDrawable().apply {
                setColor(Palette.primary)
                cornerRadius = 100f
            }
            background = btnBg
            setPadding(32, 28, 32, 28)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
            setOnClickListener {
                startActivity(
                    Intent(this@SignActivity, MediaPipeDebugActivity::class.java)
                        .putExtra(EXTRA_SIGN, sign)
                )
            }
        }
        rootLayout.addView(startPracticeButton)

        val fullResultsButton = Button(this).apply {
            text = "Zobacz pełne wyniki"
            textSize = 15f
            setTextColor(Palette.primary)
            isAllCaps = false
            val btnBg = GradientDrawable().apply {
                setColor(Palette.surface)
                setStroke(3, Palette.border)
                cornerRadius = 100f
            }
            background = btnBg
            setPadding(32, 28, 32, 28)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
            setOnClickListener { openFullResults() }
        }
        rootLayout.addView(fullResultsButton)

        val lessonButton = Button(this).apply {
            text = "Przejdź do Lekcji z tym znakiem"
            textSize = 15f
            setTextColor(Palette.accent)
            isAllCaps = false
            val btnBg = GradientDrawable().apply {
                setColor(Palette.accentTint)
                cornerRadius = 100f
            }
            background = btnBg
            setPadding(32, 28, 32, 28)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 24 }
            setOnClickListener {
                startActivity(Intent(this@SignActivity, LessonActivity::class.java))
            }
        }
        rootLayout.addView(lessonButton)

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }

    override fun onResume() {
        super.onResume()
        val progress = SignPracticeStore.progress(this, sign)
        if (progress == null) {
            lastScoreValue.text = "Brak prób"
            bestScoreValue.text = "—"
        } else {
            val last = progress.lastAttempt
            lastScoreValue.text = "${last.score} pkt · ${SignFeedback.rating(last.score)}"
            bestScoreValue.text = "${progress.bestScore} pkt · ${progress.attempts} ${attemptsLabel(progress.attempts)}"
        }
    }

    private fun attemptsLabel(count: Int): String = when {
        count == 1 -> "próba"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "próby"
        else -> "prób"
    }

    private fun metricValueView() = TextView(this).apply {
        textSize = 14f
        setTextColor(Palette.text)
        setTypeface(null, android.graphics.Typeface.BOLD)
        setPadding(0, 4, 0, 0)
    }

    private fun createClickableMetricCard(
        title: String,
        valueView: TextView,
        titleColor: Int,
        onClick: () -> Unit
    ): LinearLayout {
        val cardBg = GradientDrawable().apply {
            setColor(Palette.surface)
            setStroke(2, Palette.border)
            cornerRadius = 32f
        }
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg
            setPadding(28, 24, 28, 24)
            setOnClickListener { onClick() }

            val titleView = TextView(this@SignActivity).apply {
                text = title
                textSize = 13f
                setTextColor(titleColor)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val linkHintView = TextView(this@SignActivity).apply {
                text = "Kliknij"
                textSize = 11f
                setTextColor(Palette.textSecondary)
                setPadding(0, 6, 0, 0)
            }

            addView(titleView)
            addView(valueView)
            addView(linkHintView)
        }
    }

    companion object {
        const val EXTRA_SIGN = "sign"
    }
}
