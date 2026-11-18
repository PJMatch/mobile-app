package com.example.pjm_match

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.pjm_match.ui.Palette

class SignResultsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val signName = intent.getStringExtra(EXTRA_SIGN_NAME) ?: run { finish(); return }
        val progress = SignPracticeStore.progress(this, signName) ?: run {
            toast("Nagraj znak, aby zobaczyć wyniki")
            finish()
            return
        }
        val attempt = progress.lastAttempt

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

        // 1. Top Navigation Bar
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 32)
        }

        val backBtn = TextView(this).apply {
            text = "Powrót do znaku"
            textSize = 15f
            setTextColor(Palette.primary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 32, 0)
            setOnClickListener {
                startActivity(
                    Intent(this@SignResultsActivity, SignActivity::class.java)
                        .putExtra(SignActivity.EXTRA_SIGN, signName)
                )
                finish()
            }
        }

        val titleHeader = TextView(this).apply {
            text = "Pełne Wyniki Gestu"
            textSize = 16f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        topBar.addView(backBtn)
        topBar.addView(titleHeader)
        rootLayout.addView(topBar)

        // 2. Score Hero Card
        val heroCardBg = GradientDrawable().apply {
            setColor(Palette.hero)
            cornerRadius = 48f
        }

        val heroCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = heroCardBg
            setPadding(48, 40, 48, 40)
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
        }

        val signLabel = TextView(this).apply {
            text = "ZNAK: ${signName.uppercase()}"
            textSize = 12f
            setTextColor(Palette.heroSubtext)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        }

        val scoreBadge = TextView(this).apply {
            text = "${attempt.score} / 100 pkt"
            textSize = 36f
            setTextColor(Palette.accentStrong)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val starsLabel = TextView(this).apply {
            text = "Ocena: ${SignFeedback.rating(attempt.score)}"
            textSize = 16f
            setTextColor(Palette.onPrimary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 0)
        }

        val historyLabel = TextView(this).apply {
            text = "Najlepszy wynik: ${progress.bestScore} / 100 · Próby: ${progress.attempts}"
            textSize = 12f
            setTextColor(Palette.heroSubtext)
            setPadding(0, 8, 0, 0)
        }

        heroCard.addView(signLabel)
        heroCard.addView(scoreBadge)
        heroCard.addView(starsLabel)
        heroCard.addView(historyLabel)
        rootLayout.addView(heroCard)

        if (attempt.predictions.isNotEmpty()) {
            rootLayout.addView(sectionTitle("Co zobaczył model:"))
            rootLayout.addView(modelCard(attempt))
        }

        if (attempt.metrics.isNotEmpty()) {
            rootLayout.addView(sectionTitle("Szczegółowa analiza ruchu:"))
            attempt.metrics.forEachIndexed { index, metric ->
                rootLayout.addView(infoCard("${index + 1}. ${metric.title}", metric.detail))
            }
        }

        // 5. Interactive Options (Retry, Go to Lessons, Technical Radar)
        val actionsSectionTitle = TextView(this).apply {
            text = "Dalsze kroki:"
            textSize = 18f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 20)
        }
        rootLayout.addView(actionsSectionTitle)

        // Retry Button
        val retryBtn = Button(this).apply {
            text = "Ponów próbę"
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
                    Intent(this@SignResultsActivity, MediaPipeDebugActivity::class.java)
                        .putExtra(SignActivity.EXTRA_SIGN, signName)
                )
                finish()
            }
        }
        rootLayout.addView(retryBtn)

        // Go to Lessons Button
        val lessonBtn = Button(this).apply {
            text = "Przejdź do Lekcji"
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
            setOnClickListener {
                startActivity(Intent(this@SignResultsActivity, LessonActivity::class.java))
            }
        }
        rootLayout.addView(lessonBtn)

        // Radar Stats Button
        val radarBtn = Button(this).apply {
            text = "Radar Techniki i Statystyki"
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
                startActivity(Intent(this@SignResultsActivity, RadarChartFullScreenActivity::class.java))
            }
        }
        rootLayout.addView(radarBtn)

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }

    private fun sectionTitle(title: String) = TextView(this).apply {
        text = title
        textSize = 18f
        setTextColor(Palette.text)
        setTypeface(null, android.graphics.Typeface.BOLD)
        setPadding(0, 0, 0, 20)
    }

    private fun cardContainer() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = GradientDrawable().apply {
            setColor(Palette.surface)
            setStroke(2, Palette.border)
            cornerRadius = 32f
        }
        setPadding(36, 28, 36, 28)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 16 }
    }

    private fun infoCard(title: String, detail: String) = cardContainer().apply {
        addView(TextView(this@SignResultsActivity).apply {
            text = title
            textSize = 14f
            setTextColor(Palette.primary)
            setTypeface(null, android.graphics.Typeface.BOLD)
        })
        addView(TextView(this@SignResultsActivity).apply {
            text = detail
            textSize = 13f
            setTextColor(Palette.textSecondary)
            setPadding(0, 6, 0, 0)
        })
    }

    private fun modelCard(attempt: SignAttempt) = cardContainer().apply {
        addView(TextView(this@SignResultsActivity).apply {
            text = SignFeedback.summary(attempt)
            textSize = 14f
            setTextColor(Palette.text)
            setPadding(0, 0, 0, 20)
        })
        attempt.predictions.take(TOP_PREDICTIONS).forEach { prediction ->
            addView(predictionRow(prediction, isTarget = prediction.gloss == attempt.sign))
        }
    }

    private fun predictionRow(prediction: SignPrediction, isTarget: Boolean) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(0, 10, 0, 10)
        val color = if (isTarget) Palette.primary else Palette.accent

        val header = LinearLayout(this@SignResultsActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 8)
        }
        header.addView(TextView(this@SignResultsActivity).apply {
            text = SignFeedback.displayGloss(prediction.gloss) + if (isTarget) " (ćwiczony znak)" else ""
            textSize = 13f
            setTextColor(Palette.text)
            if (isTarget) setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        })
        header.addView(TextView(this@SignResultsActivity).apply {
            text = SignFeedback.percent(prediction.confidence)
            textSize = 13f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
        })
        addView(header)

        val track = LinearLayout(this@SignResultsActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 1f
            background = GradientDrawable().apply {
                setColor(Palette.tint)
                cornerRadius = 8f
            }
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 12)
        }
        if (prediction.confidence > 0f) {
            track.addView(View(this@SignResultsActivity).apply {
                background = GradientDrawable().apply {
                    setColor(color)
                    cornerRadius = 8f
                }
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, prediction.confidence.coerceIn(0f, 1f))
            })
        }
        addView(track)
    }

    companion object {
        private const val TOP_PREDICTIONS = 3
        const val EXTRA_SIGN_NAME = "extra_sign_name"
    }
}
