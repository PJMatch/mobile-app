package com.example.pjm_match

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class SignActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        val sign = intent.getStringExtra(EXTRA_SIGN) ?: "Dzień dobry"

        val openFullResults = {
            val intent = Intent(this@SignActivity, SignResultsActivity::class.java).apply {
                putExtra(SignResultsActivity.EXTRA_SIGN_NAME, sign)
                putExtra(SignResultsActivity.EXTRA_SCORE, 88)
                putExtra(SignResultsActivity.EXTRA_STARS, "⭐ Bardzo dobry gest!")
                putExtra(SignResultsActivity.EXTRA_TEMPO, "W normie (1.2s · Tempo zbliżone do wzorca)")
                putExtra(SignResultsActivity.EXTRA_FLUENCY, "Wysoka płynność (94% · Brak gwałtownych szarpnięć)")
                putExtra(SignResultsActivity.EXTRA_PRECISION, "Precyzyjne ułożenie dłoni (90% zgodności pozycji)")
            }
            startActivity(intent)
        }

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#F4F5FF"))
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
            text = "← Wróć"
            textSize = 16f
            setTextColor(Color.parseColor("#2B2D91"))
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
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 6)
        }
        
        val signNameTextView = TextView(this).apply {
            text = sign
            textSize = 30f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        headerLayout.addView(stepTextView)
        headerLayout.addView(signNameTextView)
        rootLayout.addView(headerLayout)
        
        // 3. Video Instructions Card
        val videoContainer = FrameLayout(this).apply {
            val bgDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#12121E"))
                cornerRadius = 40f
            }
            background = bgDrawable
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                480
            ).apply { bottomMargin = 32 }
        }

        val placeholderIcon = TextView(this).apply {
            text = "▶ Odtwórz"
            textSize = 20f
            setTextColor(Color.WHITE)
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
            setTextColor(Color.parseColor("#7E7A93"))
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
            setTextColor(Color.parseColor("#2B2D91"))
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

        val cardTempo = createClickableMetricCard("⚡ Tempo", "1.2s · W normie", "#F58220", openFullResults)
        cardTempo.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = 12 }

        val cardFluency = createClickableMetricCard("🌊 Płynność", "94% · Bardzo wysoka", "#2B2D91", openFullResults)
        cardFluency.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        metricsRow.addView(cardTempo)
        metricsRow.addView(cardFluency)
        rootLayout.addView(metricsRow)

        // 5. Main Action Buttons
        val startPracticeButton = Button(this).apply {
            text = "Nagraj znak (Rozpocznij ćwiczenie)"
            textSize = 15f
            setTextColor(Color.WHITE)
            isAllCaps = false
            val btnBg = GradientDrawable().apply {
                setColor(Color.parseColor("#2B2D91"))
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
            setTextColor(Color.parseColor("#2B2D91"))
            isAllCaps = false
            val btnBg = GradientDrawable().apply {
                setColor(Color.WHITE)
                setStroke(3, Color.parseColor("#2B2D91"))
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
            setTextColor(Color.parseColor("#F58220"))
            isAllCaps = false
            val btnBg = GradientDrawable().apply {
                setColor(Color.parseColor("#FFF3E0"))
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

    private fun createClickableMetricCard(
        title: String,
        value: String,
        titleColorHex: String,
        onClick: () -> Unit
    ): LinearLayout {
        val cardBg = GradientDrawable().apply {
            setColor(Color.WHITE)
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
                setTextColor(Color.parseColor(titleColorHex))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val valueView = TextView(this@SignActivity).apply {
                text = value
                textSize = 14f
                setTextColor(Color.parseColor("#2B2D91"))
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 4, 0, 0)
            }

            val linkHintView = TextView(this@SignActivity).apply {
                text = "Kliknij ➔"
                textSize = 11f
                setTextColor(Color.parseColor("#7E7A93"))
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
