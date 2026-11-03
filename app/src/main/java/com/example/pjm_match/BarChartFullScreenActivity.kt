package com.example.pjm_match

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BarChartFullScreenActivity : AppCompatActivity() {
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

        val chartHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 32)
        }

        val chartTitleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val chartTitle = TextView(this).apply {
            text = "Twój tydzień"
            textSize = 24f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val chartDateRange = TextView(this).apply {
            text = "Dzisiaj"
            textSize = 15f
            setTextColor(Color.parseColor("#7E7A93"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 0)
        }
        
        val chartXpText = TextView(this).apply {
            text = "240 XP"
            textSize = 18f
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        chartTitleLayout.addView(chartTitle)
        chartTitleLayout.addView(chartDateRange)
        chartHeaderLayout.addView(chartTitleLayout)
        chartHeaderLayout.addView(chartXpText)
        rootLayout.addView(chartHeaderLayout)
        
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

        val whiteCardBg = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 48f
        }
        
        val chartCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardBg
            setPadding(40, 60, 40, 60)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val barGraphScroll = android.widget.HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (200 * resources.displayMetrics.density).toInt()
            )
            isFillViewport = true
        }

        val barGraphLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM
            layoutParams = android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        
        barGraphScroll.addView(barGraphLayout)
        chartCard.addView(barGraphScroll)
        
        val dayNames = listOf("Nd", "Pn", "Wt", "Śr", "Cz", "Pt", "So")
        
        fun updateChartData(newDateText: String, startDate: java.util.Date, endDate: java.util.Date, forceZero: Boolean = false) {
            chartDateRange.text = newDateText
            val diffInMillies = Math.abs(endDate.time - startDate.time)
            val daysCount = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillies, java.util.concurrent.TimeUnit.MILLISECONDS).toInt() + 1
            
            barGraphLayout.removeAllViews()
            
            val numColumns = Math.min(daysCount, 7)
            
            var currentTotalXp = 0
            
            if (daysCount == 1) {
                // Konsekwentne wartości losowe oparte o seed
                val seed = (newDateText + "0").hashCode().toLong()
                val random = java.util.Random(seed)
                val hasData = if (forceZero) false else random.nextDouble() > 0.3
                val value = if (hasData) (random.nextDouble() * 40 + 10).toInt() else 0
                currentTotalXp = value
                
                val singleDayContainer = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply { weight = 1f }
                }

                val singleDayValue = TextView(this).apply {
                    text = "$value"
                    textSize = 64f
                    setTextColor(Color.parseColor("#F58220"))
                    gravity = Gravity.CENTER
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                
                val singleDayLabel = TextView(this).apply {
                    text = "Zdobyte punkty XP"
                    textSize = 16f
                    setTextColor(Color.parseColor("#2B2D91"))
                    gravity = Gravity.CENTER
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setPadding(0, 16, 0, 0)
                }
                
                singleDayContainer.addView(singleDayValue)
                singleDayContainer.addView(singleDayLabel)
                barGraphLayout.addView(singleDayContainer)
                
            } else {
                for (i in 0 until numColumns) {
                    val isActive = (i == numColumns - 1)
                    val barColumn = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                    }
                    
                    // Symulacja danych na spójnym seedzie
                    val seed = (newDateText + i.toString()).hashCode().toLong()
                    val random = java.util.Random(seed)
                    val hasData = if (forceZero) false else random.nextDouble() > 0.3
                    val value = if (hasData) (random.nextDouble() * 40 + 10).toInt() else 0
                    currentTotalXp += value
                    
                    val ratio = value / 50f
                    val valueStr = value.toString()
                    
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
                        layoutParams = LinearLayout.LayoutParams(
                            (14 * resources.displayMetrics.density).toInt(),
                            (110 * resources.displayMetrics.density * ratio).toInt()
                        )
                    }
                    
                    val startDayOffset = (i * daysCount.toDouble() / numColumns).toInt()
                    val endDayOffset = ((i + 1) * daysCount.toDouble() / numColumns).toInt() - 1
                    
                    val calStart = java.util.Calendar.getInstance()
                    calStart.time = startDate
                    calStart.add(java.util.Calendar.DAY_OF_YEAR, startDayOffset)
                    val bucketStart = calStart.time
                    
                    val calEnd = java.util.Calendar.getInstance()
                    calEnd.time = startDate
                    calEnd.add(java.util.Calendar.DAY_OF_YEAR, endDayOffset)
                    val bucketEnd = calEnd.time
                    
                    val sdfDate = java.text.SimpleDateFormat("dd.MM", java.util.Locale.getDefault())
    
                    val labelStr = if (daysCount <= 7) {
                        dayNames[calStart.get(java.util.Calendar.DAY_OF_WEEK) - 1]
                    } else {
                        if (startDayOffset == endDayOffset) {
                            sdfDate.format(bucketStart)
                        } else {
                            "${sdfDate.format(bucketStart)}\n-\n${sdfDate.format(bucketEnd)}"
                        }
                    }
    
                    val labelView = TextView(this).apply {
                        text = labelStr
                        textSize = 10f
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
            }
            
            chartXpText.text = "$currentTotalXp XP"
            
            barGraphScroll.post {
                barGraphScroll.fullScroll(View.FOCUS_RIGHT)
            }
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
                            val startMs = selection.first ?: System.currentTimeMillis()
                            val endMs = selection.second ?: System.currentTimeMillis()
                            val startDate = java.util.Date(startMs)
                            val endDate = java.util.Date(endMs)
                            val startStr = sdf.format(startDate)
                            val endStr = sdf.format(endDate)
                            chartTitle.text = "Twój okres"
                            updateChartData("$startStr - $endStr", startDate, endDate)
                            updateActiveFilter(index)
                        }
                        datePicker.show(supportFragmentManager, "date_picker")
                    } else {
                        updateActiveFilter(index)
                        val cal = java.util.Calendar.getInstance()
                        val today = cal.time
                        
                        when (index) {
                            0 -> { 
                                chartTitle.text = "Dzisiaj"
                                updateChartData("Dzisiaj", today, today) 
                            }
                            1 -> { 
                                chartTitle.text = "Twój tydzień"
                                cal.add(java.util.Calendar.DAY_OF_YEAR, -6)
                                updateChartData("Ostatnie 7 dni", cal.time, today) 
                            }
                            2 -> { 
                                chartTitle.text = "Twój miesiąc"
                                cal.add(java.util.Calendar.DAY_OF_YEAR, -29)
                                updateChartData("Ostatnie 30 dni", cal.time, today) 
                            }
                            3 -> { 
                                chartTitle.text = "Twój miesiąc"
                                cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
                                updateChartData("Ten miesiąc", cal.time, today, forceZero = true) 
                            }
                        }
                    }
                }
            }
            filterViews.add(pill)
            filterRow.addView(pill)
        }
        updateActiveFilter(0)
        
        filterScroll.addView(filterRow)
        rootLayout.addView(filterScroll)
        
        rootLayout.addView(chartCard)
        updateChartData("Dzisiaj", java.util.Date(), java.util.Date())
        
        setContentView(rootLayout)
    }
}