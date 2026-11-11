package com.example.pjm_match

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pjm_match.ui.*

class RegisterActivity : BaseActivity() {
    private lateinit var nameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dark = Palette.isDark
        fun color(light: String, night: String) = Color.parseColor(if (dark) night else light)
        val backgroundColor = color("#FFFFFF", "#12121E")
        val textColor = color("#201840", "#EFEDFF")
        val mutedColor = color("#827D90", "#AAA8C2")
        val secondaryColor = color("#716B80", "#AAA8C2")
        val accentColor = color("#2D1986", "#B8ACFF")
        val buttonColor = color("#2D1986", "#5848BD")
        val borderColor = color("#E2DFEB", "#3A394F")
        val fieldColor = color("#FAF9FD", "#202031")

        val scroll = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            isVerticalScrollBarEnabled = false
        }
        val column = vertical { gravity = Gravity.CENTER_HORIZONTAL }
        scroll.addView(column, FrameLayout.LayoutParams(MATCH, WRAP))
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime())
            column.setPadding(dp(28) + bars.left, dp(40) + bars.top,
                dp(28) + bars.right, dp(28) + maxOf(bars.bottom, keyboard.bottom))
            insets
        }

        column.place(label("PJMatch", 34f, accentColor, 700) {
            gravity = Gravity.CENTER
        })
        column.place(label("Poznaj język migowy od podstaw", 16f, secondaryColor) {
            gravity = Gravity.CENTER
        }, top = 8, bottom = 40)

        fun field(title: String, hintText: String, id: Int, password: Boolean): EditText {
            val caption = column.place(label(title, 14f, textColor, 600), bottom = 8)
            val row = column.place(horizontal {
                background = roundedRect(fieldColor, 16, borderColor, 1)
                pad(16, 0)
                minimumHeight = dp(56)
            }, bottom = 16)
            row.place(icon(if (password) Icons.loginLock else Icons.navUser, mutedColor),
                WRAP, WRAP, end = 16)
            val input = row.place(EditText(this).apply {
                this.id = id
                hint = hintText
                setTextColor(textColor)
                setHintTextColor(mutedColor)
                textSize = 16f
                background = null
                setSingleLine(true)
                inputType = InputType.TYPE_CLASS_TEXT or if (password)
                    InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_TEXT_FLAG_CAP_WORDS
                typeface = Fonts.weight(400)
                if (password) transformationMethod = PasswordTransformationMethod.getInstance()
                imeOptions = if (id == CONFIRM_ID) EditorInfo.IME_ACTION_DONE else EditorInfo.IME_ACTION_NEXT
                setPadding(0, dp(12), 0, dp(12))
                minimumHeight = dp(56)
                // Passwords are deliberately excluded from instance-state persistence.
                isSaveEnabled = !password
            }, 0, WRAP, 1f)
            caption.labelFor = input.id
            if (password) {
                var visible = false
                val toggle = FrameLayout(this).apply {
                    contentDescription = "Pokaż hasło"
                    isFocusable = true
                }
                val eye = toggle.place(icon(Icons.loginEye, mutedColor), WRAP, WRAP,
                    gravity = Gravity.CENTER)
                row.place(toggle, 48, 48)
                toggle.onTap(12, Palette.ripple) {
                    val cursor = input.selectionEnd.coerceAtLeast(0)
                    visible = !visible
                    input.transformationMethod = if (visible) null else PasswordTransformationMethod.getInstance()
                    eye.spec = if (visible) Icons.loginEyeOff else Icons.loginEye
                    toggle.contentDescription = if (visible) "Ukryj hasło" else "Pokaż hasło"
                    input.setSelection(cursor.coerceAtMost(input.length()))
                }
            }
            return input
        }

        nameInput = field("Imię", "Wpisz swoje imię", NAME_ID, false)
        nameInput.setText(savedInstanceState?.getString("name").orEmpty())
        val password = field("Hasło", "Wpisz hasło", PASSWORD_ID, true)
        val confirmation = field("Powtórz hasło", "Wpisz ponownie hasło", CONFIRM_ID, true)

        fun validate() {
            nameInput.error = null
            password.error = null
            confirmation.error = null
            val invalid = when {
                nameInput.text.isBlank() -> nameInput.also { it.error = "Wpisz swoje imię" }
                password.text.isEmpty() -> password.also { it.error = "Wpisz hasło" }
                confirmation.text.isEmpty() -> confirmation.also { it.error = "Powtórz hasło" }
                password.text.toString() != confirmation.text.toString() ->
                    confirmation.also { it.error = "Hasła muszą być takie same" }
                else -> null
            }
            if (invalid != null) invalid.requestFocus()
            else Toast.makeText(this,
                "Formularz jest poprawny. Tworzenie konta nie jest jeszcze dostępne.",
                Toast.LENGTH_LONG).show()
        }

        column.place(Button(this).apply {
            text = "Załóż konto"
            textSize = 16f
            typeface = Fonts.weight(600)
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = roundedRect(buttonColor, 16, accentColor, 1.5f)
            minimumHeight = dp(56)
            onTap(16, Palette.ripple) { validate() }
        }, top = 8)
        confirmation.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) { validate(); true } else false
        }
        column.place(label("Masz już konto?", 14f, secondaryColor) {
            gravity = Gravity.CENTER
        }, top = 20, bottom = 12)
        column.place(Button(this).apply {
            text = "Zaloguj się"
            textSize = 16f
            typeface = Fonts.weight(600)
            isAllCaps = false
            setTextColor(accentColor)
            background = roundedRect(buttonColor.withAlpha(0.2f), 16)
            minimumHeight = dp(56)
            onTap(16, Palette.ripple) { finish() }
        })
        setContentView(scroll)
        ViewCompat.requestApplyInsets(scroll)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("name", nameInput.text.toString())
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val NAME_ID = 0x00A001
        const val PASSWORD_ID = 0x00A002
        const val CONFIRM_ID = 0x00A003
    }
}
