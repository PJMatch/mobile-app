package com.example.pjm_match

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.pjm_match.ui.Palette

class SignSelectionActivity : BaseActivity() {

    private val practiceSigns = listOf(
        Pair("JA", "Wskazanie na klatkę piersiową · Podstawowy gest PJM"),
        Pair("UCZYĆ-SIĘ", "Ruch dłoni przy czole · Powszechny znak nauki"),
        Pair("MIGAĆ", "Naprzemienne ruchy dłoni · Gestykulacja migowa"),
        Pair("KOT", "Pociągnięcie wąsów przy policzku · Znak zwierzęcia"),
        Pair("PRACA", "Uderzenie pięścią w dłoń · Codzienny zwrot")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

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

        // Top Navigation Bar
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 32)
        }

        val backBtn = TextView(this).apply {
            text = "Wróć do Nauki"
            textSize = 15f
            setTextColor(Palette.primary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 32, 0)
            setOnClickListener { finish() }
        }

        topBar.addView(backBtn)
        rootLayout.addView(topBar)

        // Header
        val headerTag = TextView(this).apply {
            text = "MENU TRENINGU Z KAMERĄ"
            textSize = 12f
            setTextColor(Palette.accent)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 6)
        }

        val headerTitle = TextView(this).apply {
            text = "Wybierz Znak do Nauki"
            textSize = 28f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        }

        val headerSub = TextView(this).apply {
            text = "Wybierz gest PJM, który chcesz przećwiczyć z analizatorem kamery MediaPipe."
            textSize = 14f
            setTextColor(Palette.textSecondary)
            setPadding(0, 0, 0, 32)
        }

        rootLayout.addView(headerTag)
        rootLayout.addView(headerTitle)
        rootLayout.addView(headerSub)

        // Sign Options List
        practiceSigns.forEach { (signName, description) ->
            val cardBg = GradientDrawable().apply {
                setColor(Palette.surface)
                setStroke(2, Palette.border)
                cornerRadius = 36f
            }

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = cardBg
                setPadding(36, 32, 36, 32)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 20 }
            }

            val signTitle = TextView(this).apply {
                text = signName
                textSize = 22f
                setTextColor(Palette.text)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val signDesc = TextView(this).apply {
                text = description
                textSize = 13f
                setTextColor(Palette.textSecondary)
                setPadding(0, 6, 0, 20)
            }

            val startBtn = Button(this).apply {
                text = "Rozpocznij ćwiczenie z kamerą"
                textSize = 14f
                setTextColor(Palette.onPrimary)
                isAllCaps = false
                val btnBg = GradientDrawable().apply {
                    setColor(Palette.primary)
                    cornerRadius = 100f
                }
                background = btnBg
                setPadding(28, 20, 28, 20)
                setTypeface(null, android.graphics.Typeface.BOLD)
                setOnClickListener {
                    startActivity(
                        Intent(this@SignSelectionActivity, SignActivity::class.java)
                            .putExtra(SignActivity.EXTRA_SIGN, signName)
                    )
                }
            }

            card.addView(signTitle)
            card.addView(signDesc)
            card.addView(startBtn)
            rootLayout.addView(card)
        }

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }
}
