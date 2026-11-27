package com.example.pjm_match

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.pjm_match.ui.Palette

data class SignStat(
    val name: String,
    val repetitions: Int,
    val lastPerformed: String,
    val overallScore: Int,
    val trajectoryScore: Int,
    val smoothnessScore: Int,
    val speedScore: Int,
    val orientationScore: Int,
    val fingerScore: Int
)

class RadarChartFullScreenActivity : BaseActivity() {

    private var currentPeriodIndex = 0
    private var currentPeriodLabel = "Dzisiaj"
    private var currentSignStats: List<SignStat> = emptyList()
    private var selectedSignForRadar: SignStat? = null
    private var activeSortMode = 0 // 0: Repetitions desc, 1: Score desc, 2: Name A-Z
    private var searchQuery = ""

    private lateinit var scrollView: ScrollView
    private lateinit var radarDateRange: TextView
    private lateinit var chartModeTitle: TextView
    private lateinit var chartModeScore: TextView
    private lateinit var resetRadarBtn: TextView
    private lateinit var radarChartView: RadarChartView
    private lateinit var radarCard: LinearLayout

    private lateinit var sectionSubtitle: TextView
    private lateinit var signListContainer: LinearLayout
    private lateinit var sortPillViews: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        scrollView = ScrollView(this).apply {
            isFillViewport = true
            setBackgroundColor(Palette.background)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 48, 40, 48)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(rootLayout)

        // Top Bar
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 32)
        }

        val backBtn = TextView(this).apply {
            text = "← Wróć"
            textSize = 16f
            setTextColor(Palette.primary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 32, 0)
            setOnClickListener { finish() }
        }
        topBar.addView(backBtn)
        rootLayout.addView(topBar)

        // Header Layout
        val radarHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 24)
        }

        val radarTitleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val radarTitle = TextView(this).apply {
            text = "Analiza techniki"
            textSize = 26f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        radarDateRange = TextView(this).apply {
            text = "Dzisiaj"
            textSize = 15f
            setTextColor(Palette.accent)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 6, 0, 0)
        }

        radarTitleLayout.addView(radarTitle)
        radarTitleLayout.addView(radarDateRange)
        radarHeaderLayout.addView(radarTitleLayout)
        rootLayout.addView(radarHeaderLayout)

        // Period Filters Scroll View
        val filterScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 28 }
        }

        val filterRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val filterViews = mutableListOf<TextView>()
        fun updateActiveFilter(selectedIndex: Int) {
            filterViews.forEachIndexed { i, tv ->
                if (i == selectedIndex) {
                    tv.background = GradientDrawable().apply {
                        setColor(Palette.primary)
                        cornerRadius = 40f
                    }
                    tv.setTextColor(Palette.onPrimary)
                } else {
                    tv.background = GradientDrawable().apply {
                        setColor(Palette.tint)
                        cornerRadius = 40f
                    }
                    tv.setTextColor(Palette.text)
                }
            }
        }

        val filters = listOf("Dzisiaj", "7 dni", "30 dni", "Miesiąc", "Zakres")
        filters.forEachIndexed { index, text ->
            val pill = TextView(this).apply {
                this.text = text
                textSize = 13f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(40, 16, 40, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 16 }

                setOnClickListener {
                    if (index == filters.size - 1) {
                        val datePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.dateRangePicker()
                            .setTitleText("Wybierz przedział czasowy")
                            .build()
                        datePicker.addOnPositiveButtonClickListener { selection ->
                            val sdf = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
                            val startStr = sdf.format(java.util.Date(selection.first))
                            val endStr = sdf.format(java.util.Date(selection.second))
                            currentPeriodIndex = index
                            currentPeriodLabel = "$startStr - $endStr"
                            updateActiveFilter(index)
                            onPeriodChanged()
                        }
                        datePicker.show(supportFragmentManager, "date_picker")
                    } else {
                        updateActiveFilter(index)
                        currentPeriodIndex = index
                        currentPeriodLabel = when (index) {
                            0 -> "Dzisiaj"
                            1 -> "Ostatnie 7 dni"
                            2 -> "Ostatnie 30 dni"
                            3 -> "Ten miesiąc"
                            else -> ""
                        }
                        onPeriodChanged()
                    }
                }
            }
            filterViews.add(pill)
            filterRow.addView(pill)
        }
        updateActiveFilter(0)
        filterScroll.addView(filterRow)
        rootLayout.addView(filterScroll)

        // Radar Chart Card
        val whiteCardBg = GradientDrawable().apply {
            setColor(Palette.surface)
            setStroke(2, Palette.border)
            cornerRadius = 48f
        }

        radarCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardBg
            setPadding(32, 36, 32, 40)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 36 }
        }

        // Radar Chart Card Top Info Row
        val chartCardHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 16)
        }

        val chartModeContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        chartModeTitle = TextView(this).apply {
            text = "Średnia ogólna z okresu"
            textSize = 15f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        resetRadarBtn = TextView(this).apply {
            text = "Przywróć średnią wszystkich znaków"
            textSize = 12f
            setTextColor(Palette.primary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 4, 0, 0)
            visibility = View.GONE
            setOnClickListener {
                selectedSignForRadar = null
                updateRadarDisplay()
            }
        }

        chartModeContainer.addView(chartModeTitle)
        chartModeContainer.addView(resetRadarBtn)

        chartModeScore = TextView(this).apply {
            text = "88%"
            textSize = 18f
            setTextColor(Palette.accent)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        chartCardHeader.addView(chartModeContainer)
        chartCardHeader.addView(chartModeScore)
        radarCard.addView(chartCardHeader)

        radarChartView = RadarChartView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (300 * resources.displayMetrics.density).toInt()
            )
        }
        radarCard.addView(radarChartView)
        rootLayout.addView(radarCard)

        // Signs Statistics Section Title & Subtitle
        val sectionHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 8, 0, 16)
        }

        val sectionTitle = TextView(this).apply {
            text = "Statystyki poszczególnych znaków"
            textSize = 20f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        sectionSubtitle = TextView(this).apply {
            text = "Wykonane znaki w tym okresie"
            textSize = 13f
            setTextColor(Palette.textSecondary)
            setPadding(0, 4, 0, 0)
        }

        sectionHeaderLayout.addView(sectionTitle)
        sectionHeaderLayout.addView(sectionSubtitle)
        rootLayout.addView(sectionHeaderLayout)

        // Search Bar & Sort Row
        val searchAndSortLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 24 }
        }

        val searchBg = GradientDrawable().apply {
            setColor(Palette.surface)
            setStroke(2, Palette.border)
            cornerRadius = 32f
        }

        val searchInput = EditText(this).apply {
            hint = "Szukaj znaku po nazwie..."
            textSize = 14f
            setTextColor(Palette.text)
            setHintTextColor(Palette.textMuted)
            background = searchBg
            setPadding(36, 24, 36, 24)
            setSingleLine()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchQuery = s?.toString()?.trim() ?: ""
                    renderSignList()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        searchAndSortLayout.addView(searchInput)

        // Sort Options Row
        val sortRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val sortLabel = TextView(this).apply {
            text = "Sortuj:"
            textSize = 12f
            setTextColor(Palette.textSecondary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 16, 0)
        }
        sortRow.addView(sortLabel)

        val sortTitles = listOf("Powtórzenia", "Celność", "Nazwa A-Z")
        val sortButtons = mutableListOf<TextView>()

        fun updateActiveSort(selectedIndex: Int) {
            activeSortMode = selectedIndex
            sortButtons.forEachIndexed { i, tv ->
                if (i == selectedIndex) {
                    tv.background = GradientDrawable().apply {
                        setColor(Palette.primary)
                        cornerRadius = 24f
                    }
                    tv.setTextColor(Palette.onPrimary)
                } else {
                    tv.background = GradientDrawable().apply {
                        setColor(Palette.tint)
                        cornerRadius = 24f
                    }
                    tv.setTextColor(Palette.text)
                }
            }
            renderSignList()
        }

        sortTitles.forEachIndexed { idx, title ->
            val sortPill = TextView(this).apply {
                text = title
                textSize = 11f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(28, 12, 28, 12)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = 12 }

                setOnClickListener { updateActiveSort(idx) }
            }
            sortButtons.add(sortPill)
            sortRow.addView(sortPill)
        }
        // Container for Sign Cards
        signListContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        sortPillViews = sortButtons
        updateActiveSort(0)

        searchAndSortLayout.addView(sortRow)
        rootLayout.addView(searchAndSortLayout)
        rootLayout.addView(signListContainer)

        // Initial Data Load
        onPeriodChanged()
        setContentView(scrollView)
    }

    private fun onPeriodChanged() {
        radarDateRange.text = currentPeriodLabel
        selectedSignForRadar = null
        currentSignStats = getSignStatsForPeriod(currentPeriodIndex, currentPeriodLabel)

        val totalSigns = currentSignStats.size
        val totalReps = currentSignStats.sumOf { it.repetitions }
        sectionSubtitle.text = "Wykonane znaki: $totalSigns · Łącznie $totalReps powtórzeń w okresie"

        updateRadarDisplay()
        renderSignList()
    }

    private fun updateRadarDisplay() {
        val selected = selectedSignForRadar
        if (selected != null) {
            chartModeTitle.text = "Znak: ${selected.name}"
            chartModeScore.text = "${selected.overallScore}%"
            resetRadarBtn.visibility = View.VISIBLE

            radarChartView.dataPoints = floatArrayOf(
                selected.trajectoryScore / 100f,
                selected.smoothnessScore / 100f,
                selected.speedScore / 100f,
                selected.orientationScore / 100f,
                selected.fingerScore / 100f
            )
        } else {
            chartModeTitle.text = "Średnia ogólna z okresu"
            resetRadarBtn.visibility = View.GONE

            val dataPoints = calculateOverallDataPoints(currentSignStats)
            radarChartView.dataPoints = dataPoints
            val avgScore = (dataPoints.average() * 100).toInt()
            chartModeScore.text = "$avgScore%"
        }
    }

    private fun renderSignList() {
        if (!::signListContainer.isInitialized) return
        signListContainer.removeAllViews()

        var filtered = currentSignStats.filter {
            if (searchQuery.isEmpty()) true
            else it.name.contains(searchQuery, ignoreCase = true)
        }

        filtered = when (activeSortMode) {
            0 -> filtered.sortedByDescending { it.repetitions }
            1 -> filtered.sortedByDescending { it.overallScore }
            2 -> filtered.sortedBy { it.name }
            else -> filtered
        }

        if (filtered.isEmpty()) {
            val emptyTextView = TextView(this).apply {
                text = if (searchQuery.isNotEmpty()) "Brak znaków pasujących do \"$searchQuery\"" else "Brak zarejestrowanych znaków w tym okresie."
                textSize = 14f
                setTextColor(Palette.textSecondary)
                gravity = Gravity.CENTER
                setPadding(0, 48, 0, 48)
            }
            signListContainer.addView(emptyTextView)
            return
        }

        for (stat in filtered) {
            val cardView = createSignStatCard(stat)
            signListContainer.addView(cardView)
        }
    }

    private fun createSignStatCard(stat: SignStat): LinearLayout {
        val cardBg = GradientDrawable().apply {
            setColor(Palette.surface)
            setStroke(2, Palette.border)
            cornerRadius = 36f
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBg
            setPadding(32, 28, 32, 28)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 20 }
        }

        // Top Row: Avatar + Sign Name + Repetitions + Score Badge
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 16)
        }

        val avatarBg = GradientDrawable().apply {
            setColor(Palette.tint)
            cornerRadius = 24f
        }

        val avatarTile = FrameLayout(this).apply {
            background = avatarBg
            layoutParams = LinearLayout.LayoutParams(80, 80).apply { marginEnd = 24 }

            val letterView = TextView(this@RadarChartFullScreenActivity).apply {
                text = stat.name.take(1).uppercase()
                textSize = 18f
                setTextColor(Palette.primary)
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
            addView(letterView)
        }

        val nameAndRepContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val signNameView = TextView(this).apply {
            text = stat.name
            textSize = 16f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val repView = TextView(this).apply {
            text = "${stat.repetitions} powtórzeń · ${stat.lastPerformed}"
            textSize = 12f
            setTextColor(Palette.textSecondary)
            setPadding(0, 4, 0, 0)
        }

        nameAndRepContainer.addView(signNameView)
        nameAndRepContainer.addView(repView)

        val scoreBadgeBg = GradientDrawable().apply {
            setColor(Palette.accentTint)
            setStroke(2, Palette.accent)
            cornerRadius = 24f
        }

        val scoreBadge = TextView(this).apply {
            text = "${stat.overallScore}%"
            textSize = 14f
            setTextColor(Palette.accent)
            setTypeface(null, android.graphics.Typeface.BOLD)
            background = scoreBadgeBg
            setPadding(24, 10, 24, 10)
        }

        topRow.addView(avatarTile)
        topRow.addView(nameAndRepContainer)
        topRow.addView(scoreBadge)
        card.addView(topRow)

        // Divider
        val divider = View(this).apply {
            setBackgroundColor(Palette.border)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ).apply { bottomMargin = 16 }
        }
        card.addView(divider)

        // 5 Metric Bars
        val metrics = listOf(
            "Trajektoria ruchu" to stat.trajectoryScore,
            "Płynność" to stat.smoothnessScore,
            "Prędkość" to stat.speedScore,
            "Orientacja dłoni" to stat.orientationScore,
            "Ułożenie palców" to stat.fingerScore
        )

        for ((metricName, metricScore) in metrics) {
            val metricRow = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 4, 0, 8)
            }

            val labelRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 4)
            }

            val labelText = TextView(this).apply {
                text = metricName
                textSize = 11f
                setTextColor(Palette.textSecondary)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val scoreText = TextView(this).apply {
                text = "$metricScore%"
                textSize = 11f
                setTextColor(Palette.text)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            labelRow.addView(labelText)
            labelRow.addView(scoreText)
            metricRow.addView(labelRow)

            val trackBg = GradientDrawable().apply {
                setColor(Palette.tint)
                cornerRadius = 6f
            }

            val trackLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = trackBg
                weightSum = 100f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    10
                )
            }

            val fillBg = GradientDrawable().apply {
                setColor(Palette.primary)
                cornerRadius = 6f
            }

            val fillView = View(this).apply {
                background = fillBg
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    metricScore.toFloat()
                )
            }
            trackLayout.addView(fillView)
            metricRow.addView(trackLayout)

            card.addView(metricRow)
        }

        // Action Buttons Row
        val actionsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 12, 0, 0)
        }

        val showOnRadarBtn = TextView(this).apply {
            text = "Pokaż na radarze"
            textSize = 12f
            setTextColor(Palette.primary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            background = GradientDrawable().apply {
                setColor(Palette.tint)
                cornerRadius = 20f
            }
            setPadding(24, 12, 24, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = 8 }
            setOnClickListener {
                selectedSignForRadar = stat
                updateRadarDisplay()
                scrollView.smoothScrollTo(0, radarCard.top)
            }
        }

        val practiceBtn = TextView(this).apply {
            text = "Ćwicz gest"
            textSize = 12f
            setTextColor(Palette.onPrimary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            background = GradientDrawable().apply {
                setColor(Palette.primary)
                cornerRadius = 20f
            }
            setPadding(24, 12, 24, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { marginStart = 8 }
            setOnClickListener {
                val intent = Intent(this@RadarChartFullScreenActivity, SignActivity::class.java).apply {
                    putExtra(SignActivity.EXTRA_SIGN, stat.name)
                }
                startActivity(intent)
            }
        }

        actionsRow.addView(showOnRadarBtn)
        actionsRow.addView(practiceBtn)
        card.addView(actionsRow)

        return card
    }

    private fun getSignStatsForPeriod(index: Int, dateRangeLabel: String): List<SignStat> {
        return when (index) {
            0 -> listOf( // Dzisiaj
                SignStat("DZIADEK", 8, "Dzisiaj, 14:20", 94, 92, 96, 90, 94, 98),
                SignStat("DOM", 6, "Dzisiaj, 13:45", 88, 85, 90, 88, 92, 86),
                SignStat("CZEKAĆ", 5, "Dzisiaj, 11:10", 82, 80, 84, 85, 78, 83),
                SignStat("DZIĘKUJĘ", 4, "Dzisiaj, 09:30", 90, 88, 92, 90, 89, 91)
            )
            1 -> listOf( // 7 dni
                SignStat("UCZYĆ-SIĘ", 14, "2 dni temu", 92, 90, 93, 94, 91, 92),
                SignStat("PIES", 11, "3 dni temu", 85, 82, 87, 86, 84, 86),
                SignStat("DZIADEK", 8, "Dzisiaj, 14:20", 94, 92, 96, 90, 94, 98),
                SignStat("MAMA", 9, "4 dni temu", 93, 91, 95, 92, 93, 94),
                SignStat("KOBIETA", 7, "5 dni temu", 86, 84, 88, 85, 87, 86),
                SignStat("DOM", 6, "Dzisiaj, 13:45", 88, 85, 90, 88, 92, 86),
                SignStat("CZEKAĆ", 5, "Dzisiaj, 11:10", 82, 80, 84, 85, 78, 83),
                SignStat("DZIĘKUJĘ", 4, "Dzisiaj, 09:30", 90, 88, 92, 90, 89, 91)
            )
            2 -> listOf( // 30 dni
                SignStat("ZAWSZE", 18, "12 dni temu", 89, 88, 90, 87, 89, 91),
                SignStat("WIEDZIEĆ", 15, "15 dni temu", 87, 85, 88, 89, 86, 87),
                SignStat("UCZYĆ-SIĘ", 14, "2 dni temu", 92, 90, 93, 94, 91, 92),
                SignStat("CHORY", 12, "18 dni temu", 84, 82, 85, 84, 83, 86),
                SignStat("PIES", 11, "3 dni temu", 85, 82, 87, 86, 84, 86),
                SignStat("BARDZO", 10, "22 dni temu", 91, 89, 92, 91, 90, 93),
                SignStat("MAMA", 9, "4 dni temu", 93, 91, 95, 92, 93, 94),
                SignStat("DZIADEK", 8, "Dzisiaj, 14:20", 94, 92, 96, 90, 94, 98),
                SignStat("KOBIETA", 7, "5 dni temu", 86, 84, 88, 85, 87, 86),
                SignStat("DOM", 6, "Dzisiaj, 13:45", 88, 85, 90, 88, 92, 86),
                SignStat("CZEKAĆ", 5, "Dzisiaj, 11:10", 82, 80, 84, 85, 78, 83),
                SignStat("DZIĘKUJĘ", 4, "Dzisiaj, 09:30", 90, 88, 92, 90, 89, 91)
            )
            3 -> listOf( // Miesiąc
                SignStat("ZAWSZE", 22, "10 dni temu", 90, 89, 91, 88, 90, 92),
                SignStat("UCZYĆ-SIĘ", 18, "2 dni temu", 93, 91, 94, 95, 92, 93),
                SignStat("WIEDZIEĆ", 16, "12 dni temu", 88, 86, 89, 90, 87, 88),
                SignStat("PIES", 14, "3 dni temu", 86, 83, 88, 87, 85, 87),
                SignStat("DZIADEK", 10, "Dzisiaj, 14:20", 95, 93, 97, 91, 95, 99),
                SignStat("MAMA", 9, "4 dni temu", 93, 91, 95, 92, 93, 94),
                SignStat("KOBIETA", 8, "5 dni temu", 87, 85, 89, 86, 88, 87),
                SignStat("DOM", 7, "Dzisiaj, 13:45", 89, 86, 91, 89, 93, 87),
                SignStat("CZEKAĆ", 6, "Dzisiaj, 11:10", 83, 81, 85, 86, 79, 84),
                SignStat("DZIĘKUJĘ", 5, "Dzisiaj, 09:30", 91, 89, 93, 91, 90, 92)
            )
            else -> {
                val seed = dateRangeLabel.hashCode().toLong()
                val random = java.util.Random(seed)
                val availableSigns = signs.take(15)
                val count = (random.nextInt(6) + 4)
                val selected = availableSigns.shuffled(random).take(count)
                selected.mapIndexed { idx, signName ->
                    val reps = random.nextInt(12) + 2
                    val traj = random.nextInt(25) + 75
                    val smooth = random.nextInt(25) + 75
                    val speed = random.nextInt(25) + 75
                    val orient = random.nextInt(25) + 75
                    val finger = random.nextInt(25) + 75
                    val overall = (traj + smooth + speed + orient + finger) / 5
                    val dayOffset = random.nextInt(10) + 1
                    SignStat(
                        name = signName,
                        repetitions = reps,
                        lastPerformed = "$dayOffset dni temu",
                        overallScore = overall,
                        trajectoryScore = traj,
                        smoothnessScore = smooth,
                        speedScore = speed,
                        orientationScore = orient,
                        fingerScore = finger
                    )
                }
            }
        }
    }

    private fun calculateOverallDataPoints(stats: List<SignStat>): FloatArray {
        if (stats.isEmpty()) return floatArrayOf(0.75f, 0.8f, 0.6f, 0.9f, 0.85f)
        val avgTraj = stats.map { it.trajectoryScore }.average().toFloat() / 100f
        val avgSmooth = stats.map { it.smoothnessScore }.average().toFloat() / 100f
        val avgSpeed = stats.map { it.speedScore }.average().toFloat() / 100f
        val avgOrient = stats.map { it.orientationScore }.average().toFloat() / 100f
        val avgFinger = stats.map { it.fingerScore }.average().toFloat() / 100f
        return floatArrayOf(avgTraj, avgSmooth, avgSpeed, avgOrient, avgFinger)
    }
}
