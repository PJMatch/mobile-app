package com.example.pjm_match

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.example.pjm_match.ui.Fonts
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect

class LoginActivity : BaseActivity() {

    override fun screenBackground(): Int = palette.screen

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (settings.loggedIn) {
            openHome()
            return
        }

        scaffold(navTab = null) {
            gravity = Gravity.CENTER_HORIZONTAL
            place(label("PJMatch", 40f, palette.primary, 800) { gravity = Gravity.CENTER }, WRAP, WRAP, top = 48)
            place(label("Poznaj język migowy od podstaw", 17f, palette.textSecondary) { gravity = Gravity.CENTER }, WRAP, WRAP, top = 10)

            place(ImageView(context).apply {
                setImageResource(if (palette.isDark) R.drawable.login_illustration_dark else R.drawable.login_illustration_light)
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
                contentDescription = "Osoba miga słowo Cześć"
            }, MATCH, 124, top = 40, start = -8, end = -8)

            place(fieldLabel("Adres e-mail"), top = 36, bottom = 10)
            emailInput = input("Wpisz swój adres e-mail", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            place(inputField(R.drawable.ic_mail, emailInput), MATCH, 56)

            place(fieldLabel("Hasło"), top = 20, bottom = 10)
            passwordInput = input("Wpisz hasło", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD).apply {
                imeOptions = EditorInfo.IME_ACTION_DONE
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) login()
                    actionId == EditorInfo.IME_ACTION_DONE
                }
            }
            place(inputField(R.drawable.ic_lock, passwordInput, passwordToggle()), MATCH, 56)

            place(label("Nie pamiętasz hasła?", 15f, palette.primary, 700) {
                pad(4, 6)
                onTap(8, palette.ripple) {
                    Toast.makeText(this@LoginActivity, "Resetowanie hasła w przygotowaniu", Toast.LENGTH_SHORT).show()
                }
            }, WRAP, WRAP, top = 14, gravity = Gravity.END)

            place(primaryButton("Zaloguj się", R.drawable.ic_arrow_right) { login() }, MATCH, 56, top = 28)

            place(label("Pierwszy raz z PJMatch?", 15f, palette.textSecondary) { gravity = Gravity.CENTER }, WRAP, WRAP, top = 28, bottom = 12)
            place(secondaryButton("Załóż konto") {
                Toast.makeText(this@LoginActivity, "Rejestracja w przygotowaniu", Toast.LENGTH_SHORT).show()
            }, MATCH, 56)
        }
    }

    private fun fieldLabel(text: String) = label(text, 15f, palette.text, 600)

    private fun input(hint: String, type: Int) = EditText(this).apply {
        this.hint = hint
        inputType = type
        setSingleLine(true)
        background = null
        textSize = 16f
        typeface = Fonts.weight(400)
        setTextColor(palette.text)
        setHintTextColor(palette.textMuted)
        setPadding(0, 0, 0, 0)
    }

    private fun inputField(iconRes: Int, input: EditText, trailing: ImageView? = null): LinearLayout = horizontal {
        background = roundedRect(palette.input, 16, palette.border, 1)
        pad(18, 0)
        place(icon(iconRes, palette.textSecondary), 22, 22, end = 14)
        place(input, 0, WRAP, 1f)
        trailing?.let { place(it, 40, 40, start = 4) }
    }

    private fun passwordToggle(): ImageView = icon(R.drawable.ic_eye, palette.textSecondary).apply {
        pad(9)
        contentDescription = "Pokaż hasło"
        onTap(20, palette.ripple) {
            val visible = passwordInput.inputType and InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            val selection = passwordInput.selectionEnd
            passwordInput.inputType = InputType.TYPE_CLASS_TEXT or
                if (visible) InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            passwordInput.typeface = Fonts.weight(400)
            passwordInput.setSelection(selection)
            setImageResource(if (visible) R.drawable.ic_eye else R.drawable.ic_eye_off)
            contentDescription = if (visible) "Pokaż hasło" else "Ukryj hasło"
        }
    }

    private fun login() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        when {
            email.isEmpty() || password.isEmpty() ->
                Toast.makeText(this, "Wpisz adres e-mail i hasło", Toast.LENGTH_SHORT).show()
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                Toast.makeText(this, "Niepoprawny adres e-mail", Toast.LENGTH_SHORT).show()
            else -> {
                settings.email = email
                settings.loggedIn = true
                openHome()
            }
        }
    }

    private fun openHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
