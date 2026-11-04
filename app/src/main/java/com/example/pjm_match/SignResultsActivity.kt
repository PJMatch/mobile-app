package com.example.pjm_match

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val signName = intent.getStringExtra(EXTRA_SIGN_NAME) ?: "Dzień dobry"
        val score = intent.getIntExtra(EXTRA_SCORE, 88)
        val stars = intent.getStringExtra(EXTRA_STARS) ?: "(Bardzo dobrze!)"
        
        val tempoText = intent.getStringExtra(EXTRA_TEMPO) ?: "W normie (1.2s · Tempo zbliżone do wzorca)"
        val fluencyText = intent.getStringExtra(EXTRA_FLUENCY) ?: "Wysoka płynność (94% · Brak gwałtownych szarpnięć)"
        val precisionText = intent.getStringExtra(EXTRA_PRECISION) ?: "Precyzyjne ułożenie dłoni (90% zgodności pozycji)"
        val trajectoryText = intent.getStringExtra(EXTRA_TRAJECTORY) ?: "Odległość DTW: 0.284 · Pełny, dokończony ruch"
        val orientationText = intent.getStringExtra(EXTRA_ORIENTATION) ?: "Orientacja dłoni skierowana prawidłowo do przodu"
        val asymmetryText = intent.getStringExtra(EXTRA_ASYMMETRY) ?: "Ruch oburęczny zsynchronizowany symetrycznie"
        val jerkText = intent.getStringExtra(EXTRA_JERK) ?: "Gładka pochodna przyspieszenia (Niski wskaźnik Jerk)"

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

        // 1. Top Navigation Bar
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 32)
        }

        val backBtn = TextView(this).apply {
            text = "← Powrót do znaku"
            textSize = 15f
            setTextColor(Color.parseColor("#2B2D91"))
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
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        topBar.addView(backBtn)
        topBar.addView(titleHeader)
        rootLayout.addView(topBar)

        // 2. Score Hero Card
        val heroCardBg = GradientDrawable().apply {
            setColor(Color.parseColor("#2B2D91"))
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
            setTextColor(Color.parseColor("#D0C9FF"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        }

        val scoreBadge = TextView(this).apply {
            text = "$score / 100 pkt"
            textSize = 36f
            setTextColor(Color.parseColor("#FFC107"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val starsLabel = TextView(this).apply {
            text = "Ocena: $stars"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 0)
        }

        heroCard.addView(signLabel)
        heroCard.addView(scoreBadge)
        heroCard.addView(starsLabel)
        rootLayout.addView(heroCard)

        // 3. Section Title
        val sectionTitle = TextView(this).apply {
            text = "Szczegółowa analiza wskaźników:"
            textSize = 18f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 20)
        }
        rootLayout.addView(sectionTitle)

        // 4. Metric Items
        val metricsList = listOf(
            Triple("1. Tempo i Czas Trwania", tempoText, "#F58220"),
            Triple("2. Płynność Ruchu", fluencyText, "#2B2D91"),
            Triple("3. Precyzja Ułożenia Dłoni", precisionText, "#2B2D91"),
            Triple("4. Trajektoria Ruchu (DTW / Fréchet)", trajectoryText, "#5848BD"),
            Triple("5. Orientacja Dłoni", orientationText, "#2B2D91"),
            Triple("6. Asymetria Prędkości Oburęcznej", asymmetryText, "#2B2D91"),
            Triple("7. Gładkość Ruchu (Pochodna Jerk)", jerkText, "#F58220")
        )

        metricsList.forEach { (title, detail, accentColorHex) ->
            val metricCardBg = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = 32f
            }
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = metricCardBg
                setPadding(36, 28, 36, 28)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 }
            }

            val cardTitle = TextView(this).apply {
                text = title
                textSize = 14f
                setTextColor(Color.parseColor(accentColorHex))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val cardDetail = TextView(this).apply {
                text = detail
                textSize = 13f
                setTextColor(Color.parseColor("#7E7A93"))
                setPadding(0, 6, 0, 0)
            }

            card.addView(cardTitle)
            card.addView(cardDetail)
            rootLayout.addView(card)
        }

        // 5. Interactive Options (Retry, Go to Lessons, Technical Radar)
        val actionsSectionTitle = TextView(this).apply {
            text = "Dalsze kroki:"
            textSize = 18f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 24, 0, 20)
        }
        rootLayout.addView(actionsSectionTitle)

        // Retry Button
        val retryBtn = Button(this).apply {
            text = "Ponów próbę"
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
            setOnClickListener {
                startActivity(Intent(this@SignResultsActivity, LessonActivity::class.java))
            }
        }
        rootLayout.addView(lessonBtn)

        // Radar Stats Button
        val radarBtn = Button(this).apply {
            text = "Radar Techniki i Statystyki"
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
                startActivity(Intent(this@SignResultsActivity, RadarChartFullScreenActivity::class.java))
            }
        }
        rootLayout.addView(radarBtn)

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }

    companion object {
        const val EXTRA_SIGN_NAME = "extra_sign_name"
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_STARS = "extra_stars"
        const val EXTRA_TEMPO = "extra_tempo"
        const val EXTRA_FLUENCY = "extra_fluency"
        const val EXTRA_PRECISION = "extra_precision"
        const val EXTRA_TRAJECTORY = "extra_trajectory"
        const val EXTRA_ORIENTATION = "extra_orientation"
        const val EXTRA_ASYMMETRY = "extra_asymmetry"
        const val EXTRA_JERK = "extra_jerk"
    }
}
