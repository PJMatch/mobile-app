package com.example.pjm_match

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.example.pjm_match.ui.Palette

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Palette.background)
            isFillViewport = true
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 80, 48, 64)
        }

        val titleTextView = TextView(this).apply {
            text = "PJMatch"
            textSize = 32f
            setTextColor(Palette.primary)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        val subtitleTextView = TextView(this).apply {
            text = "Poznaj język migowy od podstaw"
            textSize = 15f
            setTextColor(Palette.textSecondary)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 48)
        }

        val emailLabelTextView = TextView(this).apply {
            text = "Adres e-mail"
            textSize = 14f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val inputDrawable = GradientDrawable().apply {
            setColor(Palette.surface)
            setStroke(2, Palette.border)
            cornerRadius = 32f
        }

        val emailContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = inputDrawable
            setPadding(32, 16, 32, 16)
        }


        val standardTypeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL)

        val emailEditText = EditText(this).apply {
            hint = "Wpisz swój adres e-mail"
            setHintTextColor(Palette.textMuted)
            setTextColor(Palette.text)
            textSize = 15f
            typeface = standardTypeface
            background = null
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        emailContainer.addView(emailEditText)

        val passwordLabelTextView = TextView(this).apply {
            text = "Hasło"
            textSize = 14f
            setTextColor(Palette.text)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val passwordContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = inputDrawable.constantState?.newDrawable() as GradientDrawable
            setPadding(32, 16, 32, 16)
        }


        val passwordEditText = EditText(this).apply {
            hint = "Wpisz hasło"
            setHintTextColor(Palette.textMuted)
            setTextColor(Palette.text)
            textSize = 15f
            background = null
            setSingleLine(true)
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            typeface = standardTypeface
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val eyeToggle = TextView(this).apply {
            text = "👁"
            textSize = 18f
            setPadding(16, 0, 0, 0)
            setOnClickListener {
                val currentSelection = passwordEditText.selectionEnd
                if ((passwordEditText.inputType and android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    text = "👁"
                } else {
                    passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    text = "👁‍🗨"
                }
                passwordEditText.typeface = standardTypeface
                passwordEditText.setSelection(currentSelection)
            }
        }

        passwordContainer.addView(passwordEditText)
        passwordContainer.addView(eyeToggle)

        val forgotPasswordTextView = TextView(this).apply {
            text = "Nie pamiętasz hasła?"
            textSize = 14f
            setTextColor(Palette.primary)
            gravity = Gravity.END
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val primaryButtonDrawable = GradientDrawable().apply {
            setColor(Palette.primary)
            cornerRadius = 32f
        }

        val loginButton = Button(this).apply {
            text = "Zaloguj się"
            textSize = 16f
            setTextColor(Palette.onPrimary)
            background = primaryButtonDrawable
            setPadding(32, 36, 32, 36)
            setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this@LoginActivity, "Wpisz adres e-mail i hasło", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        val registerPromptTextView = TextView(this).apply {
            text = "Pierwszy raz z PJMatch?"
            textSize = 14f
            setTextColor(Palette.textSecondary)
            gravity = Gravity.CENTER
            setPadding(0, 36, 0, 16)
        }

        val secondaryButtonDrawable = GradientDrawable().apply {
            setColor(Palette.tint)
            cornerRadius = 32f
        }

        val registerButton = Button(this).apply {
            text = "Załóż konto"
            textSize = 16f
            setTextColor(Palette.primary)
            background = secondaryButtonDrawable
            setPadding(32, 36, 32, 36)
            setOnClickListener {
                Toast.makeText(this@LoginActivity, "Rejestracja w przygotowaniu", Toast.LENGTH_SHORT).show()
            }
        }

        rootLayout.addView(titleTextView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        rootLayout.addView(subtitleTextView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        rootLayout.addView(emailLabelTextView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 8 })

        rootLayout.addView(emailContainer, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 24 })

        rootLayout.addView(passwordLabelTextView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 8 })

        rootLayout.addView(passwordContainer, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 12 })

        rootLayout.addView(forgotPasswordTextView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 36 })

        rootLayout.addView(loginButton, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = 8 })

        rootLayout.addView(registerPromptTextView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        rootLayout.addView(registerButton, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        scrollView.addView(rootLayout, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ))

        setContentView(scrollView)
    }
}
