package com.example.pjm_match

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        // Root container holding ScrollView and Bottom Navigation
        val mainContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5FA"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Main ScrollView for scrollable content
        val scrollView = ScrollView(this).apply {
            isFillViewport = true
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

        // Top Header: Date, Greeting, and Profile Avatar
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 32)
        }

        val textHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val dateTextView = TextView(this).apply {
            text = "NIEDZIELA, 18 PAŹDZIERNIKA"
            textSize = 12f
            setTextColor(Color.parseColor("#7E7A93"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val greetingTextView = TextView(this).apply {
            text = "Cześć, Aniu!"
            textSize = 28f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        textHeaderLayout.addView(dateTextView)
        textHeaderLayout.addView(greetingTextView)

        val avatarDrawable = GradientDrawable().apply {
            setColor(Color.parseColor("#EBE9FC"))
            cornerRadius = 48f
        }

        val avatarTextView = TextView(this).apply {
            text = "A"
            textSize = 20f
            setTextColor(Color.parseColor("#3B1895"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            background = avatarDrawable
            layoutParams = LinearLayout.LayoutParams(96, 96)
        }

        headerLayout.addView(textHeaderLayout)
        headerLayout.addView(avatarTextView)

        // Top Stats Row (Streak & XP cards)
        val statsRowLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 24 }
        }

        // Streak Card (Orange/Yellow #FFEED6)
        val streakCardDrawable = GradientDrawable().apply {
            setColor(Color.parseColor("#FFEED6"))
            cornerRadius = 32f
        }

        val streakCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = streakCardDrawable
            setPadding(32, 28, 32, 28)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = 12
            }
        }

        val streakIcon = TextView(this).apply {
            text = "🔥"
            textSize = 24f
            setPadding(0, 0, 16, 0)
        }

        val streakTextLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val streakValue = TextView(this).apply {
            text = "7 dni"
            textSize = 18f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val streakLabel = TextView(this).apply {
            text = "Twoja seria"
            textSize = 12f
            setTextColor(Color.parseColor("#D3821C"))
        }

        streakTextLayout.addView(streakValue)
        streakTextLayout.addView(streakLabel)
        streakCard.addView(streakIcon)
        streakCard.addView(streakTextLayout)

        // XP Card (Lavender #EBE9FC)
        val xpCardDrawable = GradientDrawable().apply {
            setColor(Color.parseColor("#EBE9FC"))
            cornerRadius = 32f
        }

        val xpCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = xpCardDrawable
            setPadding(32, 28, 32, 28)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = 12
            }
        }

        val xpIcon = TextView(this).apply {
            text = "⚡"
            textSize = 24f
            setPadding(0, 0, 16, 0)
        }

        val xpTextLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val xpValue = TextView(this).apply {
            text = "240 XP"
            textSize = 18f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val xpLabel = TextView(this).apply {
            text = "Łącznie"
            textSize = 12f
            setTextColor(Color.parseColor("#3B1895"))
        }

        xpTextLayout.addView(xpValue)
        xpTextLayout.addView(xpLabel)
        xpCard.addView(xpIcon)
        xpCard.addView(xpTextLayout)

        statsRowLayout.addView(streakCard)
        statsRowLayout.addView(xpCard)

        // Daily Goal Card (Twój dzienny cel)
        val whiteCardDrawable = GradientDrawable().apply {
            setColor(Color.WHITE)
            setStroke(2, Color.parseColor("#E4E2F0"))
            cornerRadius = 36f
        }

        val goalCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardDrawable
            setPadding(36, 32, 36, 32)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
        }

        val goalHeaderLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 16)
        }

        val goalTitle = TextView(this).apply {
            text = "🎯 Twój dzienny cel"
            textSize = 16f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val goalScore = TextView(this).apply {
            text = "40 / 50 XP"
            textSize = 14f
            setTextColor(Color.parseColor("#3B1895"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        goalHeaderLayout.addView(goalTitle)
        goalHeaderLayout.addView(goalScore)

        val progressBarBg = GradientDrawable().apply {
            setColor(Color.parseColor("#EBE9FC"))
            cornerRadius = 16f
        }

        val progressBarTrack = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = progressBarBg
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16).apply {
                bottomMargin = 16
            }
        }

        val progressBarFill = GradientDrawable().apply {
            setColor(Color.parseColor("#3B1895"))
            cornerRadius = 16f
        }

        val progressFillView = TextView(this).apply {
            background = progressBarFill
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.8f)
        }
        progressBarTrack.addView(progressFillView)

        val goalSubtitle = TextView(this).apply {
            text = "Jeszcze 10 XP. Jeden mały krok i cel osiągnięty!"
            textSize = 12f
            setTextColor(Color.parseColor("#7E7A93"))
        }

        goalCard.addView(goalHeaderLayout)
        goalCard.addView(progressBarTrack)
        goalCard.addView(goalSubtitle)

        // Section Title: "Czas na kolejny gest"
        val sectionTitle = TextView(this).apply {
            text = "Czas na kolejny gest"
            textSize = 18f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 16)
        }

        // Main Learning Card (Dark Indigo #3B227A)
        val lessonCardDrawable = GradientDrawable().apply {
            setColor(Color.parseColor("#3B227A"))
            cornerRadius = 40f
        }

        val lessonCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = lessonCardDrawable
            setPadding(40, 40, 40, 40)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 32 }
        }

        val lessonTag = TextView(this).apply {
            text = "TWOJA ŚCIEŻKA • PJM"
            textSize = 11f
            setTextColor(Color.parseColor("#FFC107"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        }

        val lessonTitle = TextView(this).apply {
            text = "Ucz się"
            textSize = 28f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 4)
        }

        val lessonSubtitle = TextView(this).apply {
            text = "Powitania • Lekcja 3 z 5"
            textSize = 13f
            setTextColor(Color.parseColor("#D2CEEC"))
            setPadding(0, 0, 0, 24)
        }

        val lessonProgressHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 8)
        }

        val lessonCategory = TextView(this).apply {
            text = "Uprzejme słowa"
            textSize = 13f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val lessonProgressCount = TextView(this).apply {
            text = "4 / 5 pytań"
            textSize = 13f
            setTextColor(Color.parseColor("#FFC107"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        lessonProgressHeader.addView(lessonCategory)
        lessonProgressHeader.addView(lessonProgressCount)

        val lessonTrackBg = GradientDrawable().apply {
            setColor(Color.parseColor("#533B9B"))
            cornerRadius = 16f
        }

        val lessonTrack = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = lessonTrackBg
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16).apply {
                bottomMargin = 28
            }
        }

        val lessonFillDrawable = GradientDrawable().apply {
            setColor(Color.parseColor("#FFC107"))
            cornerRadius = 16f
        }

        val lessonFillView = TextView(this).apply {
            background = lessonFillDrawable
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.8f)
        }
        lessonTrack.addView(lessonFillView)

        val buttonBg = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 28f
        }

        val continueButton = Button(this).apply {
            text = "Kontynuuj lekcję →"
            textSize = 15f
            setTextColor(Color.parseColor("#3B227A"))
            background = buttonBg
            setPadding(32, 32, 32, 32)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setOnClickListener {
                startActivity(Intent(this@MainActivity, SignListActivity::class.java))
            }
        }

        lessonCard.addView(lessonTag)
        lessonCard.addView(lessonTitle)
        lessonCard.addView(lessonSubtitle)
        lessonCard.addView(lessonProgressHeader)
        lessonCard.addView(lessonTrack)
        lessonCard.addView(continueButton)

        // Bottom Grid of Two Cards (Tłumacz miganie & Statystyki)
        val bottomGridRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }

        val translatorCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardDrawable.constantState?.newDrawable() as GradientDrawable
            setPadding(28, 28, 28, 28)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = 12
            }
            setOnClickListener {
                startActivity(Intent(this@MainActivity, AiTranslatorActivity::class.java))
            }
        }

        val translatorIcon = TextView(this).apply {
            text = "📷"
            textSize = 24f
            setPadding(0, 0, 0, 12)
        }

        val translatorTitle = TextView(this).apply {
            text = "Tłumacz miganie"
            textSize = 15f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 4)
        }

        val translatorDesc = TextView(this).apply {
            text = "Pokaż znak. Poznaj jego znaczenie z AI."
            textSize = 11f
            setTextColor(Color.parseColor("#7E7A93"))
        }

        translatorCard.addView(translatorIcon)
        translatorCard.addView(translatorTitle)
        translatorCard.addView(translatorDesc)

        val statsCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = whiteCardDrawable.constantState?.newDrawable() as GradientDrawable
            setPadding(28, 28, 28, 28)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = 12
            }
            setOnClickListener {
                Toast.makeText(this@MainActivity, "Statystyki w przygotowaniu", Toast.LENGTH_SHORT).show()
            }
        }

        val statsIcon = TextView(this).apply {
            text = "📊"
            textSize = 24f
            setPadding(0, 0, 0, 12)
        }

        val statsTitle = TextView(this).apply {
            text = "Statystyki"
            textSize = 15f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 4)
        }

        val statsDesc = TextView(this).apply {
            text = "Zobacz, jak rosną Twoje umiejętności."
            textSize = 11f
            setTextColor(Color.parseColor("#7E7A93"))
        }

        statsCard.addView(statsIcon)
        statsCard.addView(statsTitle)
        statsCard.addView(statsDesc)

        bottomGridRow.addView(translatorCard)
        bottomGridRow.addView(statsCard)

        rootLayout.addView(headerLayout)
        rootLayout.addView(statsRowLayout)
        rootLayout.addView(goalCard)
        rootLayout.addView(sectionTitle)
        rootLayout.addView(lessonCard)
        rootLayout.addView(bottomGridRow)

        scrollView.addView(rootLayout, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ))

        // Bottom Navigation Bar matching start.png
        val bottomNavLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(16, 16, 16, 24)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        fun createNavItem(icon: String, label: String, isActive: Boolean, onClick: () -> Unit): LinearLayout {
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener { onClick() }

                val iconView = TextView(this@MainActivity).apply {
                    text = icon
                    textSize = 20f
                    gravity = Gravity.CENTER
                }

                val labelView = TextView(this@MainActivity).apply {
                    text = label
                    textSize = 10f
                    setTextColor(Color.parseColor(if (isActive) "#3B1895" else "#7E7A93"))
                    gravity = Gravity.CENTER
                    setTypeface(null, if (isActive) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
                }

                addView(iconView)
                addView(labelView)
            }
        }

        val navStart = createNavItem("🏠", "Start", true) {}
        val navNauka = createNavItem("🔀", "Nauka", false) {
            startActivity(Intent(this, SignListActivity::class.java))
        }
        val navAi = createNavItem("📷", "Tłumacz AI", false) {
            startActivity(Intent(this, AiTranslatorActivity::class.java))
        }
        val navStats = createNavItem("📊", "Statystyki", false) {
            Toast.makeText(this, "Statystyki w przygotowaniu", Toast.LENGTH_SHORT).show()
        }
        val navProfile = createNavItem("👤", "Profil", false) {
            Toast.makeText(this, "Profil w przygotowaniu", Toast.LENGTH_SHORT).show()
        }

        bottomNavLayout.addView(navStart)
        bottomNavLayout.addView(navNauka)
        bottomNavLayout.addView(navAi)
        bottomNavLayout.addView(navStats)
        bottomNavLayout.addView(navProfile)

        // Add ScrollView and Bottom Nav to main container
        mainContainer.addView(scrollView)
        mainContainer.addView(bottomNavLayout)

        setContentView(mainContainer)
    }
}
