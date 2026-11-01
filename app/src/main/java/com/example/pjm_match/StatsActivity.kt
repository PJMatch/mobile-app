package com.example.pjm_match

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val mainContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F4F5FF"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 48, 40, 40)
        }

        val tagTextView = TextView(this).apply {
            text = "TWÓJ ROZWÓJ"
            textSize = 11f
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            letterSpacing = 0.1f
            setPadding(0, 0, 0, 8)
        }

        val titleTextView = TextView(this).apply {
            text = "Widać postępy!"
            textSize = 28f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 6)
        }

        val subtitleTextView = TextView(this).apply {
            text = "Każdy gest ma znaczenie. Tak trzymaj, Aniu."
            textSize = 13f
            setTextColor(Color.parseColor("#7E7A93"))
            setPadding(0, 0, 0, 32)
        }

        rootLayout.addView(tagTextView)
        rootLayout.addView(titleTextView)
        rootLayout.addView(subtitleTextView)

        val bannerBg = GradientDrawable().apply {
            setColor(Color.parseColor("#2B2D91"))
            cornerRadius = 48f
        }

        val bannerCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = bannerBg
            setPadding(48, 40, 48, 40)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 28 }
        }

        val bannerTextContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val bannerSub1 = TextView(this).apply {
            text = "Twoja aktualna seria"
            textSize = 12f
            setTextColor(Color.parseColor("#D0C9FF"))
        }

        val bannerTitle = TextView(this).apply {
            text = "7 dni z rzędu"
            textSize = 22f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 4, 0, 4)
        }

        val bannerSub2 = TextView(this).apply {
            text = "Twój najlepszy wynik. Brawo!"
            textSize = 11f
            setTextColor(Color.parseColor("#D0C9FF"))
        }

        bannerTextContainer.addView(bannerSub1)
        bannerTextContainer.addView(bannerTitle)
        bannerTextContainer.addView(bannerSub2)

        val flameCircleBg = GradientDrawable().apply {
            setColor(Color.parseColor("#3D3FC7"))
            cornerRadius = 36f
        }

        val flameIconView = TextView(this).apply {
            text = "🔥"
            textSize = 26f
            gravity = Gravity.CENTER
            background = flameCircleBg
            layoutParams = LinearLayout.LayoutParams(110, 110)
        }

        bannerCard.addView(bannerTextContainer)
        bannerCard.addView(flameIconView)

        rootLayout.addView(bannerCard)

        val statsRow1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }

        val card1 = createMetricCard("240 XP", "⚡", "Łączne doświadczenie", "#F58220")
        card1.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = 8 }

        val card2 = createMetricCard("2", "📖", "Ukończone lekcje", "#2B2D91")
        card2.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = 8 }

        statsRow1.addView(card1)
        statsRow1.addView(card2)

        val statsRow2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 28 }
        }

        val card3 = createMetricCard("38 min", "⏱️", "Czas nauki łącznie", "#2B2D91")
        card3.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = 8 }

        val card4 = createMetricCard("92%", "🎯", "Poprawne odpowiedzi", "#F58220")
        card4.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = 8 }

        statsRow2.addView(card3)
        statsRow2.addView(card4)

        rootLayout.addView(statsRow1)
        rootLayout.addView(statsRow2)

        val whiteCardBg = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 48f
        }

        val chartCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardBg
            setPadding(40, 36, 40, 36)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
        }

        val chartHeaderRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 24)
        }

        val chartTitleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val chartMainTitle = TextView(this).apply {
            text = "Twój tydzień"
            textSize = 17f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val chartSubtitle = TextView(this).apply {
            text = "12–18 października"
            textSize = 11f
            setTextColor(Color.parseColor("#7E7A93"))
            setPadding(0, 2, 0, 0)
        }

        chartTitleLayout.addView(chartMainTitle)
        chartTitleLayout.addView(chartSubtitle)

        val chartXpText = TextView(this).apply {
            text = "240 XP"
            textSize = 15f
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        chartHeaderRow.addView(chartTitleLayout)
        chartHeaderRow.addView(chartXpText)
        chartCard.addView(chartHeaderRow)

        val barGraphLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200
            )
        }

        val daysData = listOf(
            Triple("Pn", "20", 20f / 50f),
            Triple("Wt", "30", 30f / 50f),
            Triple("Śr", "40", 40f / 50f),
            Triple("Cz", "20", 20f / 50f),
            Triple("Pt", "50", 50f / 50f),
            Triple("So", "40", 40f / 50f),
            Triple("Nd", "40", 40f / 50f)
        )

        for ((dayName, valueStr, ratio) in daysData) {
            val isActive = (dayName == "Nd")
            val barColumn = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }

            val valView = TextView(this).apply {
                text = valueStr
                textSize = 10f
                setTextColor(if (isActive) Color.parseColor("#2B2D91") else Color.parseColor("#7E7A93"))
                gravity = Gravity.CENTER
                if (isActive) setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 8)
            }

            val barBg = GradientDrawable().apply {
                setColor(if (isActive) Color.parseColor("#F58220") else Color.parseColor("#FBD6A4"))
                cornerRadius = 16f
            }

            val barView = View(this).apply {
                background = barBg
                layoutParams = LinearLayout.LayoutParams(40, (110 * ratio).toInt())
            }

            val labelView = TextView(this).apply {
                text = dayName
                textSize = 11f
                setTextColor(if (isActive) Color.parseColor("#2B2D91") else Color.parseColor("#7E7A93"))
                gravity = Gravity.CENTER
                if (isActive) setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 8, 0, 0)
            }

            barColumn.addView(valView)
            barColumn.addView(barView)
            barColumn.addView(labelView)
            barGraphLayout.addView(barColumn)
        }

        chartCard.addView(barGraphLayout)
        rootLayout.addView(chartCard)

        val radarHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 16)
        }

        val radarTitleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val radarTitle = TextView(this).apply {
            text = "Analiza techniki"
            textSize = 18f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val radarDateRange = TextView(this).apply {
            text = "Dzisiaj (kliknij, by powiększyć)"
            textSize = 13f
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 4, 0, 0)
        }

        radarTitleLayout.addView(radarTitle)
        radarTitleLayout.addView(radarDateRange)
        
        radarHeaderLayout.addView(radarTitleLayout)
        rootLayout.addView(radarHeaderLayout)

        val radarCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardBg.constantState?.newDrawable() as GradientDrawable
            setPadding(20, 40, 20, 40)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
        }

        val radarChartView = RadarChartView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (280 * resources.displayMetrics.density).toInt()
            )
        }

        radarCard.addView(radarChartView)
        rootLayout.addView(radarCard)

        val openRadarFullScreen = {
            startActivity(Intent(this, RadarChartFullScreenActivity::class.java))
        }

        radarHeaderLayout.setOnClickListener { openRadarFullScreen() }
        radarCard.setOnClickListener { openRadarFullScreen() }

        val topicsHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 16)
        }

        val topicsTitle = TextView(this).apply {
            text = "Twoje tematy"
            textSize = 18f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val topicsCount = TextView(this).apply {
            text = "2 / 24 lekcje"
            textSize = 13f
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        topicsHeaderLayout.addView(topicsTitle)
        topicsHeaderLayout.addView(topicsCount)
        rootLayout.addView(topicsHeaderLayout)

        val topicsCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardBg.constantState?.newDrawable() as GradientDrawable
            setPadding(40, 32, 40, 32)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
        }

        val topics = listOf(
            Pair("Powitania", "2 / 5" to 0.4f),
            Pair("Liczby", "0 / 4" to 0.0f),
            Pair("Rodzina", "0 / 5" to 0.0f),
            Pair("Jedzenie", "0 / 4" to 0.0f),
            Pair("Codzienne zwroty", "0 / 6" to 0.0f)
        )

        for ((index, topic) in topics.withIndex()) {
            val (name, progInfo) = topic
            val (countText, fillRatio) = progInfo

            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, if (index == 0) 0 else 18, 0, 18)
            }

            val topRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 8)
            }

            val nameView = TextView(this).apply {
                text = name
                textSize = 13f
                setTextColor(Color.parseColor("#2B2D91"))
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val countView = TextView(this).apply {
                text = countText
                textSize = 12f
                setTextColor(Color.parseColor("#7E7A93"))
            }

            topRow.addView(nameView)
            topRow.addView(countView)

            val trackBg = GradientDrawable().apply {
                setColor(Color.parseColor("#E6E8F5"))
                cornerRadius = 8f
            }

            val trackLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = trackBg
                weightSum = 1f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    12
                )
            }

            if (fillRatio > 0f) {
                val fillBg = GradientDrawable().apply {
                    setColor(Color.parseColor("#2B2D91"))
                    cornerRadius = 8f
                }
                val fillView = View(this).apply {
                    background = fillBg
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        fillRatio
                    )
                }
                trackLayout.addView(fillView)
            }

            itemLayout.addView(topRow)
            itemLayout.addView(trackLayout)
            topicsCard.addView(itemLayout)

            if (index < topics.size - 1) {
                val divider = View(this).apply {
                    setBackgroundColor(Color.parseColor("#F0F1FA"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )
                }
                topicsCard.addView(divider)
            }
        }

        rootLayout.addView(topicsCard)

        val badgesHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 16)
        }

        val badgesTitle = TextView(this).apply {
            text = "Małe powody do dumy"
            textSize = 18f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val badgesCount = TextView(this).apply {
            text = "2 / 6"
            textSize = 13f
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        badgesHeaderLayout.addView(badgesTitle)
        badgesHeaderLayout.addView(badgesCount)
        rootLayout.addView(badgesHeaderLayout)

        val badgesRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 24 }
        }

        val badge1 = createBadgeItem("🖐️", "Pierwszy gest", "Pierwsza lekcja", "#E8EAF9", "#2B2D91", true)
        badge1.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val badge2 = createBadgeItem("🔥", "7 dni razem", "Tydzień nauki", "#FEF0DF", "#2B2D91", true)
        badge2.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val badge3 = createBadgeItem("🔒", "Mistrz liczb", "Jeszcze przed Tobą", "#E5ECEE", "#7E7A93", false)
        badge3.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        badgesRow.addView(badge1)
        badgesRow.addView(badge2)
        badgesRow.addView(badge3)

        rootLayout.addView(badgesRow)

        scrollView.addView(rootLayout)

        val bottomNavLayout = createBottomNavigation()

        mainContainer.addView(scrollView)
        mainContainer.addView(bottomNavLayout)

        setContentView(mainContainer)
    }

    private fun createMetricCard(value: String, icon: String, label: String, iconColorHex: String): LinearLayout {
        val cardBg = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 36f
        }

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg
            setPadding(32, 28, 32, 28)

            val topRow = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val valueView = TextView(this@StatsActivity).apply {
                text = value
                textSize = 20f
                setTextColor(Color.parseColor("#2B2D91"))
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val iconView = TextView(this@StatsActivity).apply {
                text = icon
                textSize = 16f
                setTextColor(Color.parseColor(iconColorHex))
            }

            topRow.addView(valueView)
            topRow.addView(iconView)

            val labelView = TextView(this@StatsActivity).apply {
                text = label
                textSize = 11f
                setTextColor(Color.parseColor("#7E7A93"))
                setPadding(0, 8, 0, 0)
            }

            addView(topRow)
            addView(labelView)
        }
    }

    private fun createBadgeItem(icon: String, title: String, subtitle: String, circleBgHex: String, textColorHex: String, isUnlocked: Boolean): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(8, 8, 8, 8)
            if (!isUnlocked) alpha = 0.75f

            val circleBg = GradientDrawable().apply {
                setColor(Color.parseColor(circleBgHex))
                cornerRadius = 44f
            }

            val iconView = TextView(this@StatsActivity).apply {
                text = icon
                textSize = 24f
                gravity = Gravity.CENTER
                background = circleBg
                layoutParams = LinearLayout.LayoutParams(110, 110)
            }

            val titleView = TextView(this@StatsActivity).apply {
                text = title
                textSize = 12f
                setTextColor(Color.parseColor(textColorHex))
                gravity = Gravity.CENTER
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 10, 0, 2)
            }

            val subtitleView = TextView(this@StatsActivity).apply {
                text = subtitle
                textSize = 10f
                setTextColor(Color.parseColor("#7E7A93"))
                gravity = Gravity.CENTER
            }

            addView(iconView)
            addView(titleView)
            addView(subtitleView)
        }
    }

    private fun createBottomNavigation(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(16, 16, 16, 20)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            fun createNavItem(icon: String, label: String, isActive: Boolean, onClick: () -> Unit): LinearLayout {
                return LinearLayout(this@StatsActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setOnClickListener { onClick() }

                    if (isActive) {
                        val activePillBg = GradientDrawable().apply {
                            setColor(Color.parseColor("#E8EAF9"))
                            cornerRadius = 24f
                        }
                        val activeContainer = LinearLayout(this@StatsActivity).apply {
                            orientation = LinearLayout.VERTICAL
                            gravity = Gravity.CENTER
                            background = activePillBg
                            setPadding(24, 8, 24, 8)
                        }

                        val iconView = TextView(this@StatsActivity).apply {
                            text = icon
                            textSize = 18f
                            setTextColor(Color.parseColor("#2B2D91"))
                            gravity = Gravity.CENTER
                        }

                        val labelView = TextView(this@StatsActivity).apply {
                            text = label
                            textSize = 10f
                            setTextColor(Color.parseColor("#2B2D91"))
                            gravity = Gravity.CENTER
                            setTypeface(null, android.graphics.Typeface.BOLD)
                            setPadding(0, 2, 0, 0)
                        }

                        activeContainer.addView(iconView)
                        activeContainer.addView(labelView)
                        addView(activeContainer)
                    } else {
                        val iconView = TextView(this@StatsActivity).apply {
                            text = icon
                            textSize = 18f
                            setTextColor(Color.parseColor("#7E7A93"))
                            gravity = Gravity.CENTER
                        }

                        val labelView = TextView(this@StatsActivity).apply {
                            text = label
                            textSize = 10f
                            setTextColor(Color.parseColor("#7E7A93"))
                            gravity = Gravity.CENTER
                            setPadding(0, 4, 0, 0)
                        }

                        addView(iconView)
                        addView(labelView)
                    }
                }
            }

            val navStart = createNavItem("🏠", "Start", false) {
                startActivity(Intent(this@StatsActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
                finish()
            }

            val navNauka = createNavItem("🔀", "Nauka", false) {
                startActivity(Intent(this@StatsActivity, SignListActivity::class.java))
                finish()
            }

            val navAi = createNavItem("📷", "Tłumacz AI", false) {
                startActivity(Intent(this@StatsActivity, AiTranslatorActivity::class.java))
                                startActivity(Intent(this@StatsActivity, AiTranslatorActivity::class.java))
                finish()
            }

            val navStats = createNavItem("📊", "Statystyki", true) {}

            val navProfile = createNavItem("👤", "Profil", false) {
                Toast.makeText(this@StatsActivity, "Profil w przygotowaniu", Toast.LENGTH_SHORT).show()
            }

            addView(navStart)
            addView(navNauka)
            addView(navAi)
            addView(navStats)
            addView(navProfile)
        }
    }
}

class RadarChartView(context: android.content.Context) : android.view.View(context) {
    var dataPoints: FloatArray = floatArrayOf(0.75f, 0.8f, 0.6f, 0.9f, 0.85f)
        set(value) {
            field = value
            invalidate()
        }

    private val labels = arrayOf("Trajektoria\nruchu", "Płynność", "Prędkość", "Orientacja\ndłoni", "Ułożenie\npalców")

    private val path = android.graphics.Path()
    private val dataPath = android.graphics.Path()

    private val density = context.resources.displayMetrics.density

    private val gridPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#E6E8F5")
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 1f * density
    }

    private val dataPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#4D2B2D91")
        style = android.graphics.Paint.Style.FILL
    }

    private val dataStrokePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#2B2D91")
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2f * density
    }

    private val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#7E7A93")
        textSize = 10f * density
        textAlign = android.graphics.Paint.Align.CENTER
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = (Math.min(width, height) / 2f) * 0.65f

        val numPoints = 5
        val angleStep = Math.PI * 2 / numPoints

        for (step in 1..4) {
            val r = radius * (step / 4f)
            path.reset()
            for (i in 0 until numPoints) {
                val angle = i * angleStep - Math.PI / 2
                val x = cx + Math.cos(angle).toFloat() * r
                val y = cy + Math.sin(angle).toFloat() * r
                if (i == 0) path.moveTo(x, y)
                else path.lineTo(x, y)
            }
            path.close()
            canvas.drawPath(path, gridPaint)
        }

        for (i in 0 until numPoints) {
            val angle = i * angleStep - Math.PI / 2
            val x = cx + Math.cos(angle).toFloat() * radius
            val y = cy + Math.sin(angle).toFloat() * radius
            canvas.drawLine(cx, cy, x, y, gridPaint)

            val textR = radius * 1.35f
            val tx = cx + Math.cos(angle).toFloat() * textR
            val ty = cy + Math.sin(angle).toFloat() * textR
            val lines = labels[i].split("\n")
            var textY = ty - (lines.size - 1) * textPaint.textSize / 2
            for (line in lines) {
                canvas.drawText(line, tx, textY + textPaint.textSize / 3, textPaint)
                textY += textPaint.textSize + 2f * density
            }
        }

        dataPath.reset()
        for (i in 0 until numPoints) {
            val angle = i * angleStep - Math.PI / 2
            val value = dataPoints.getOrElse(i) { 0f }.coerceIn(0f, 1f)
            val x = cx + Math.cos(angle).toFloat() * radius * value
            val y = cy + Math.sin(angle).toFloat() * radius * value
            if (i == 0) dataPath.moveTo(x, y)
            else dataPath.lineTo(x, y)
        }
        dataPath.close()
        canvas.drawPath(dataPath, dataPaint)
        canvas.drawPath(dataPath, dataStrokePaint)
    }
}
