package com.example.pjm_match

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RadarChartFullScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F4F5FF"))
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
            text = "⬅ Wróć"
            textSize = 16f
            setTextColor(Color.parseColor("#2B2D91"))
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
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val radarDateRange = TextView(this).apply {
            text = "Dzisiaj"
            textSize = 15f
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 0)
        }

        radarTitleLayout.addView(radarTitle)
        radarTitleLayout.addView(radarDateRange)
        radarHeaderLayout.addView(radarTitleLayout)
        rootLayout.addView(radarHeaderLayout)

        val whiteCardBg = GradientDrawable().apply {
            setColor(Color.WHITE)
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
        
        fun updateRadarChartTextAndData(newText: String) {
            radarDateRange.text = newText
            radarChartView.dataPoints = floatArrayOf(
                (Math.random() * 0.4 + 0.5).toFloat(),
                (Math.random() * 0.4 + 0.5).toFloat(),
                (Math.random() * 0.4 + 0.5).toFloat(),
                (Math.random() * 0.4 + 0.5).toFloat(),
                (Math.random() * 0.4 + 0.5).toFloat()
            )
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
                        setColor(Color.parseColor("#2B2D91"))
                        cornerRadius = 40f
                    }
                    tv.setTextColor(Color.WHITE)
                } else {
                    tv.background = GradientDrawable().apply {
                        setColor(Color.parseColor("#E8EAF9"))
                        cornerRadius = 40f
                    }
                    tv.setTextColor(Color.parseColor("#2B2D91"))
                }
            }
        }
        
        val filters = listOf("Dzisiaj", "7 dni", "30 dni", "Miesiąc", "📅 Zakres")
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
                            updateRadarChartTextAndData("$startStr - $endStr")
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
                        updateRadarChartTextAndData(title)
                    }
                }
            }
            filterViews.add(pill)
            filterRow.addView(pill)
        }
        updateActiveFilter(0)
        
        filterScroll.addView(filterRow)
        rootLayout.addView(filterScroll)

        radarCard.addView(radarChartView)
        rootLayout.addView(radarCard)
        
        setContentView(rootLayout)
    }
}