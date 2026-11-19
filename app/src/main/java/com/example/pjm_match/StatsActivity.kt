package com.example.pjm_match

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pjm_match.ui.IconSpec
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.iconTile

class StatsActivity : BaseActivity() {

    override val tabRoot = Tab.STATS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scaffold(Tab.STATS) {
            val tagTextView = TextView(this@StatsActivity).apply {
                text = "TWÓJ ROZWÓJ"
                textSize = 11f
                setTextColor(Palette.accent)
                setTypeface(null, android.graphics.Typeface.BOLD)
                letterSpacing = 0.1f
                setPadding(0, 0, 0, 8)
            }

            val titleTextView = TextView(this@StatsActivity).apply {
                text = "Twoje statystyki"
                textSize = 28f
                setTextColor(Palette.text)
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 6)
            }

            val subtitleTextView = TextView(this@StatsActivity).apply {
                text = "Każdy gest ma znaczenie."
                textSize = 13f
                setTextColor(Palette.textSecondary)
                setPadding(0, 0, 0, 32)
            }

            addView(tagTextView)
            addView(titleTextView)
            addView(subtitleTextView)

            val bannerBg = GradientDrawable().apply {
                setColor(Palette.hero)
                cornerRadius = 48f
            }

            val bannerCard = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = bannerBg
                setPadding(48, 40, 48, 40)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 28 }
            }

            val bannerTextContainer = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val bannerSub1 = TextView(this@StatsActivity).apply {
                text = "Twoja aktualna seria"
                textSize = 12f
                setTextColor(Palette.heroSubtext)
            }

            val bannerTitle = TextView(this@StatsActivity).apply {
                text = "${LearningStatistics.streak()} dni z rzędu"
                textSize = 22f
                setTextColor(Palette.onPrimary)
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 4, 0, 4)
            }

            val bannerSub2 = TextView(this@StatsActivity).apply {
                text = "Dni z aktywnością z rzędu"
                textSize = 11f
                setTextColor(Palette.heroSubtext)
            }

            bannerTextContainer.addView(bannerSub1)
            bannerTextContainer.addView(bannerTitle)
            bannerTextContainer.addView(bannerSub2)

            val flameIconView = iconTile(Icons.startFlame, Palette.accentStrong, Palette.heroIcon).apply {
                layoutParams = LinearLayout.LayoutParams(110, 110)
            }

            bannerCard.addView(bannerTextContainer)
            bannerCard.addView(flameIconView)

            addView(bannerCard)

            val statsRow1 = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 }
            }

            val card1 = createMetricCard("${LearningStatistics.totalXp()} XP", "", "Łączne doświadczenie", Palette.accent)
            card1.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = 8 }

            val card2 = createMetricCard("${LearningStatistics.completedCount()}", "", "Ukończone lekcje", Palette.primary)
            card2.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = 8 }

            statsRow1.addView(card1)
            statsRow1.addView(card2)

            val statsRow2 = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 28 }
            }

            val card3 = createMetricCard("${LearningStatistics.minutes()} min", "", "Czas nauki łącznie", Palette.primary)
            card3.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = 8 }

            val card4 = createMetricCard(LearningStatistics.accuracy()?.let { "$it%" } ?: "—", "", "Poprawne próby", Palette.accent)
            card4.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = 8 }

            statsRow2.addView(card3)
            statsRow2.addView(card4)

            addView(statsRow1)
            addView(statsRow2)

            fun createCardBg() = GradientDrawable().apply {
                setColor(Palette.surface)
                setStroke(2, Palette.border)
                cornerRadius = 48f
            }

            val chartCard = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.VERTICAL
                background = createCardBg()
                setPadding(40, 36, 40, 36)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 32 }
            }

            val chartHeaderRow = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 24)
            }

            val chartTitleLayout = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val chartMainTitle = TextView(this@StatsActivity).apply {
                text = "Twój tydzień"
                textSize = 17f
                setTextColor(Palette.text)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val chartSubtitle = TextView(this@StatsActivity).apply {
                text = "Ostatnie 7 dni · kliknij, by powiększyć"
                textSize = 11f
                setTextColor(Palette.textSecondary)
                setPadding(0, 2, 0, 0)
            }

            chartTitleLayout.addView(chartMainTitle)
            chartTitleLayout.addView(chartSubtitle)

            val chartXpText = TextView(this@StatsActivity).apply {
                text = "${(0..6).sumOf { offset -> LearningStatistics.xpOn(java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -offset) }.time) }} XP"
                textSize = 15f
                setTextColor(Palette.accent)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            chartHeaderRow.addView(chartTitleLayout)
            chartHeaderRow.addView(chartXpText)
            chartCard.addView(chartHeaderRow)

            val barGraphLayout = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.BOTTOM
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200
                )
            }

            val daysData = (6 downTo 0).map { offset ->
                val date = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -offset) }.time
                val label = java.text.SimpleDateFormat("EE", java.util.Locale.forLanguageTag("pl")).format(date)
                label to LearningStatistics.xpOn(date)
            }
            val maxXp = daysData.maxOf { it.second }.coerceAtLeast(1)

            for ((index, day) in daysData.withIndex()) {
                val (dayName, value) = day
                val valueStr = value.toString()
                val ratio = value.toFloat() / maxXp
                val isActive = index == daysData.lastIndex
                val barColumn = LinearLayout(this@StatsActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                }

                val valView = TextView(this@StatsActivity).apply {
                    text = valueStr
                    textSize = 10f
                    setTextColor(if (isActive) Palette.text else Palette.textSecondary)
                    gravity = Gravity.CENTER
                    if (isActive) setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 0, 0, 8)
                }

                val barBg = GradientDrawable().apply {
                    setColor(if (isActive) Palette.accent else Palette.accentTint)
                    cornerRadius = 16f
                }

                val barView = View(this@StatsActivity).apply {
                    background = barBg
                    layoutParams = LinearLayout.LayoutParams(40, (110 * ratio).toInt())
                }

                val labelView = TextView(this@StatsActivity).apply {
                    text = dayName
                    textSize = 11f
                    setTextColor(if (isActive) Palette.text else Palette.textSecondary)
                    gravity = Gravity.CENTER
                    if (isActive) setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 8, 0, 0)
                }

                barColumn.addView(valView)
                barColumn.addView(barBgView(barView))
                barColumn.addView(labelView)
                barGraphLayout.addView(barColumn)
            }

            chartCard.addView(barGraphLayout)
            addView(chartCard)

            val openBarChartFullScreen = {
                startActivity(Intent(this@StatsActivity, BarChartFullScreenActivity::class.java))
            }

            chartHeaderRow.setOnClickListener { openBarChartFullScreen() }
            chartCard.setOnClickListener { openBarChartFullScreen() }

            val radarHeaderLayout = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 16)
            }

            val radarTitleLayout = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val radarTitle = TextView(this@StatsActivity).apply {
                text = "Analiza techniki"
                textSize = 18f
                setTextColor(Palette.text)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val radarDateRange = TextView(this@StatsActivity).apply {
                text = "Dzisiaj · kliknij, by powiększyć"
                textSize = 13f
                setTextColor(Palette.accent)
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 4, 0, 0)
            }

            radarTitleLayout.addView(radarTitle)
            radarTitleLayout.addView(radarDateRange)

            radarHeaderLayout.addView(radarTitleLayout)
            addView(radarHeaderLayout)

            val radarCard = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.VERTICAL
                background = createCardBg()
                setPadding(20, 40, 20, 40)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 32 }
            }

            val radarChartView = RadarChartView(this@StatsActivity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (280 * resources.displayMetrics.density).toInt()
                )
            }

            radarChartView.dataPoints = LearningStatistics.technique(java.util.Date(), java.util.Date()) ?: FloatArray(5)
            if (LearningStatistics.technique(java.util.Date(), java.util.Date()) == null) {
                radarCard.addView(TextView(this@StatsActivity).apply { text = "Brak danych o technice"; gravity = Gravity.CENTER; setTextColor(Palette.textSecondary) })
            }
            radarCard.addView(radarChartView)
            addView(radarCard)

            val openRadarFullScreen = {
                startActivity(Intent(this@StatsActivity, RadarChartFullScreenActivity::class.java))
            }

            radarHeaderLayout.setOnClickListener { openRadarFullScreen() }
            radarCard.setOnClickListener { openRadarFullScreen() }

            val topicsHeaderLayout = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 16)
            }

            val topicsTitle = TextView(this@StatsActivity).apply {
                text = "Twoje tematy"
                textSize = 18f
                setTextColor(Palette.text)
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val topicsCount = TextView(this@StatsActivity).apply {
                text = "${LearningStatistics.completedCount()} / 24 lekcje"
                textSize = 13f
                setTextColor(Palette.accent)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            topicsHeaderLayout.addView(topicsTitle)
            topicsHeaderLayout.addView(topicsCount)
            addView(topicsHeaderLayout)

            val topicsCard = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.VERTICAL
                background = createCardBg()
                setPadding(40, 32, 40, 32)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 32 }
            }

            val topics = listOf("Powitania" to 5, "Liczby" to 4, "Rodzina" to 5, "Jedzenie" to 4, "Codzienne zwroty" to 6).map { (name, total) ->
                val completed = LearningStatistics.completedIn(name).coerceAtMost(total)
                name to ("$completed / $total" to completed.toFloat() / total)
            }

            for ((index, topic) in topics.withIndex()) {
                val (name, progInfo) = topic
                val (countText, fillRatio) = progInfo

                val itemLayout = LinearLayout(this@StatsActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, if (index == 0) 0 else 18, 0, 18)
                }

                val topRow = LinearLayout(this@StatsActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(0, 0, 0, 8)
                }

                val nameView = TextView(this@StatsActivity).apply {
                    text = name
                    textSize = 13f
                    setTextColor(Palette.text)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val countView = TextView(this@StatsActivity).apply {
                    text = countText
                    textSize = 12f
                    setTextColor(Palette.textSecondary)
                }

                topRow.addView(nameView)
                topRow.addView(countView)

                val trackBg = GradientDrawable().apply {
                    setColor(Palette.tint)
                    cornerRadius = 8f
                }

                val trackLayout = LinearLayout(this@StatsActivity).apply {
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
                        setColor(Palette.primary)
                        cornerRadius = 8f
                    }
                    val fillView = View(this@StatsActivity).apply {
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
                    val divider = View(this@StatsActivity).apply {
                        setBackgroundColor(Palette.border)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                        )
                    }
                    topicsCard.addView(divider)
                }
            }

            addView(topicsCard)

            val badgesHeaderLayout = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 16)
            }

            val badgesTitle = TextView(this@StatsActivity).apply {
                text = "Małe powody do dumy"
                textSize = 18f
                setTextColor(Palette.text)
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val badgesCount = TextView(this@StatsActivity).apply {
                text = "${listOf(LearningStatistics.completedCount() > 0, LearningStatistics.streak() >= 7, LearningStatistics.completedIn("Liczby") >= 4).count { it }} / 3"
                textSize = 13f
                setTextColor(Palette.accent)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            badgesHeaderLayout.addView(badgesTitle)
            badgesHeaderLayout.addView(badgesCount)
            addView(badgesHeaderLayout)

            val badgesRow = LinearLayout(this@StatsActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 24 }
            }

            val badge1 = createBadgeItem(Icons.pathHand, Palette.primary, "Pierwszy gest", "Pierwsza lekcja", Palette.tint, Palette.text, LearningStatistics.completedCount() > 0)
            badge1.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val badge2 = createBadgeItem(Icons.startFlame, Palette.accent, "7 dni razem", "Tydzień nauki", Palette.accentTint, Palette.text, LearningStatistics.streak() >= 7)
            badge2.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val badge3 = createBadgeItem(Icons.pathLock, Palette.textMuted, "Mistrz liczb", "Ukończ liczby", Palette.tint, Palette.textMuted, LearningStatistics.completedIn("Liczby") >= 4)
            badge3.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            badgesRow.addView(badge1)
            badgesRow.addView(badge2)
            badgesRow.addView(badge3)

            addView(badgesRow)
        }
    }

    private fun barBgView(v: View): View = v

    private fun createMetricCard(value: String, icon: String, label: String, iconColorInt: Int): LinearLayout {
        val cardBg = GradientDrawable().apply {
            setColor(Palette.surface)
            setStroke(2, Palette.border)
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
                setTextColor(Palette.text)
                setTypeface(null, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val iconView = TextView(this@StatsActivity).apply {
                text = icon
                textSize = 16f
                setTextColor(iconColorInt)
            }

            topRow.addView(valueView)
            topRow.addView(iconView)

            val labelView = TextView(this@StatsActivity).apply {
                text = label
                textSize = 11f
                setTextColor(Palette.textSecondary)
                setPadding(0, 8, 0, 0)
            }

            addView(topRow)
            addView(labelView)
        }
    }

    private fun createBadgeItem(iconSpec: IconSpec, iconColorInt: Int, title: String, subtitle: String, circleBgInt: Int, textColorInt: Int, isUnlocked: Boolean): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(8, 8, 8, 8)
            if (!isUnlocked) alpha = 0.6f

            val iconView = iconTile(iconSpec, iconColorInt, circleBgInt).apply {
                background = GradientDrawable().apply {
                    setColor(circleBgInt)
                    cornerRadius = 44f
                }
                layoutParams = LinearLayout.LayoutParams(110, 110)
            }

            val titleView = TextView(this@StatsActivity).apply {
                text = title
                textSize = 12f
                setTextColor(textColorInt)
                gravity = Gravity.CENTER
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 10, 0, 2)
            }

            val subtitleView = TextView(this@StatsActivity).apply {
                text = subtitle
                textSize = 10f
                setTextColor(Palette.textSecondary)
                gravity = Gravity.CENTER
            }

            addView(iconView)
            addView(titleView)
            addView(subtitleView)
        }
    }
}

class RadarChartView(context: android.content.Context) : View(context) {
    var dataPoints: FloatArray = FloatArray(5)
        set(value) {
            field = value
            invalidate()
        }

    private val labels = arrayOf("Trajektoria\nruchu", "Płynność", "Prędkość", "Orientacja\ndłoni", "Ułożenie\npalców")

    private val path = android.graphics.Path()
    private val dataPath = android.graphics.Path()

    private val density = context.resources.displayMetrics.density

    private val gridPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 1f * density
    }

    private val dataPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.FILL
    }

    private val dataStrokePaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 2f * density
    }

    private val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 10f * density
        textAlign = android.graphics.Paint.Align.CENTER
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        gridPaint.color = Palette.border
        dataPaint.color = (Palette.primary and 0x00FFFFFF) or 0x4D000000
        dataStrokePaint.color = Palette.primary
        textPaint.color = Palette.textSecondary

        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = (kotlin.math.min(width, height) / 2f) * 0.65f

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
