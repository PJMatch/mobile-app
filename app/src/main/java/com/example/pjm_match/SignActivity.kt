package com.example.pjm_match

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class SignActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        val sign = intent.getStringExtra(EXTRA_SIGN) ?: "Nieznany znak"

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
            text = "Wróć"
            textSize = 16f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 32, 0)
            setOnClickListener { finish() }
        }
        topBar.addView(backBtn)
        rootLayout.addView(topBar)

        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 48)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val stepTextView = TextView(this).apply {
            text = "Krok 1 / 2: Naucz się znaku"
            textSize = 12f
            setTextColor(Color.parseColor("#F58220"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 8)
        }
        
        val signNameTextView = TextView(this).apply {
            text = sign
            textSize = 32f
            setTextColor(Color.parseColor("#2B2D91"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        headerLayout.addView(stepTextView)
        headerLayout.addView(signNameTextView)
        rootLayout.addView(headerLayout)
        
        val videoContainer = FrameLayout(this).apply {
            val bgDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#12121E"))
                cornerRadius = 48f
            }
            background = bgDrawable
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply { bottomMargin = 48 }
        }

        val placeholderIcon = TextView(this).apply {
            text = "Odtwórz"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        }
        
        val placeholderText = TextView(this).apply {
            text = "Odtwarzacz instruktażu\n(Tu będzie wideo)"
            textSize = 16f
            setTextColor(Color.parseColor("#7E7A93"))
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ).apply { topMargin = 200 }
        }

        videoContainer.addView(placeholderIcon)
        videoContainer.addView(placeholderText)
        rootLayout.addView(videoContainer)
        
        val startPracticeButton = Button(this).apply {
            text = "Przejdź do ćwiczeń"
            textSize = 16f
            setTextColor(Color.WHITE)
            isAllCaps = false
            val btnBg = GradientDrawable().apply {
                setColor(Color.parseColor("#2B2D91"))
                cornerRadius = 100f
            }
            background = btnBg
            setPadding(32, 40, 32, 40)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 32
            }
            setOnClickListener {
                startActivity(Intent(this@SignActivity, MediaPipeDebugActivity::class.java))
            }
        }
        
        rootLayout.addView(startPracticeButton)

        setContentView(rootLayout)
    }

    companion object {
        const val EXTRA_SIGN = "sign"
    }
}
