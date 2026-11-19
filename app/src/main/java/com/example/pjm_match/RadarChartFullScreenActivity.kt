package com.example.pjm_match

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pjm_match.ui.Palette

class RadarChartFullScreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Palette.background)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(40, 48, 40, 40)
        }

        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 32)
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

        val radarHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 32)
        }

        val radarTitleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val radarTitle = TextView(this).apply {
            text = "Analiza techniki"
            textSize = 24f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val radarDateRange = TextView(this).apply {
            text = "Dzisiaj"
            textSize = 15f
            setTextColor(Palette.accent)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 0)
        }

        radarTitleLayout.addView(radarTitle)
        radarTitleLayout.addView(radarDateRange)
        radarHeaderLayout.addView(radarTitleLayout)
        rootLayout.addView(radarHeaderLayout)

        val whiteCardBg = GradientDrawable().apply {
            setColor(Palette.surface)
            setStroke(2, Palette.border)
            cornerRadius = 48f
        }
        val radarCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardBg
            setPadding(20, 80, 20, 80)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val radarChartView = RadarChartView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (320 * resources.displayMetrics.density).toInt()
            )
        }
        
        val emptyMessage = TextView(this).apply {
            text = "Brak danych o technice"
            gravity = Gravity.CENTER
            setTextColor(Palette.textSecondary)
        }
        fun updateRadarChartTextAndData(newText: String, start: java.util.Date, end: java.util.Date) {
            radarDateRange.text = newText
            val data = LearningStatistics.technique(start, end)
            radarChartView.dataPoints = data ?: FloatArray(5)
            emptyMessage.visibility = if (data == null) android.view.View.VISIBLE else android.view.View.GONE
        }

        val filterScroll = android.widget.HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
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
                            updateRadarChartTextAndData("$startStr - $endStr", java.util.Date(selection.first), java.util.Date(selection.second))
                            updateActiveFilter(index)
                        }
                        datePicker.show(supportFragmentManager, "date_picker")
                    } else {
                        updateActiveFilter(index)
                        val title = when (index) {
                            0 -> "Dzisiaj"
                            1 -> "Ostatnie 7 dni"
                            2 -> "Ostatnie 30 dni"
                            3 -> "Ten miesiąc"
                            else -> ""
                        }
                        val calendar = java.util.Calendar.getInstance()
                        val end = calendar.time
                        when (index) {
                            1 -> calendar.add(java.util.Calendar.DAY_OF_YEAR, -6)
                            2 -> calendar.add(java.util.Calendar.DAY_OF_YEAR, -29)
                            3 -> calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                        }
                        updateRadarChartTextAndData(title, calendar.time, end)
                    }
                }
            }
            filterViews.add(pill)
            filterRow.addView(pill)
        }
        updateActiveFilter(0)
        
        filterScroll.addView(filterRow)
        rootLayout.addView(filterScroll)

        radarCard.addView(emptyMessage)
        radarCard.addView(radarChartView)
        updateRadarChartTextAndData("Dzisiaj", java.util.Date(), java.util.Date())
        rootLayout.addView(radarCard)
        
        setContentView(rootLayout)
    }
}
