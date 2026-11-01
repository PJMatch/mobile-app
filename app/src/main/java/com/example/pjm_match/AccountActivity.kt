package com.example.pjm_match

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.pjm_match.ui.Fonts
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical

class AccountActivity : BaseActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(
            navTab = Tab.PROFILE,
            footer = { place(primaryButton("Zapisz zmiany") { save() }, MATCH, 56) },
        ) {
            settingsHeader(this, "Dane konta", "Zarządzaj swoimi danymi i zadbaj o bezpieczeństwo konta.")
            place(card(padding = 22) {
                place(horizontal {
                    place(avatar(64, 28f), 64, 64)
                    place(vertical {
                        place(label(settings.name, 26f, palette.text, 700) { maxLines = 1 })
                        place(label(LearnerStats.level, 15f, palette.textSecondary), top = 4)
                    }, 0, WRAP, 1f, start = 18)
                })
            })
            section(this@AccountActivity, "Twoje dane")
            place(card(padding = 20) {
                nameInput = field(this, "Imię", settings.name, "Twoje imię", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                emailInput = field(this, "Adres e-mail", settings.email, "Twój adres e-mail", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, top = 18)
            })
            section(this@AccountActivity, "Bezpieczeństwo")
            place(card(padding = 0) {
                place(settingsRow(R.drawable.ic_lock, "Zmień hasło", "••••••••", chevron()) {
                    Toast.makeText(this@AccountActivity, "Zmiana hasła w przygotowaniu", Toast.LENGTH_SHORT).show()
                })
            })
            place(infoNote(R.drawable.ic_shield_check, "Po zmianie adresu e-mail wyślemy na niego link potwierdzający."), top = 20)
        }
    }

    private fun field(parent: LinearLayout, title: String, value: String, hint: String, type: Int, top: Int = 0): EditText {
        parent.place(label(title, 15f, palette.text, 700), top = top, bottom = 10)
        return parent.place(EditText(this).apply {
            setText(value)
            this.hint = hint
            inputType = type
            setSingleLine(true)
            textSize = 17f
            typeface = Fonts.weight(400)
            setTextColor(palette.text)
            setHintTextColor(palette.textSecondary)
            background = roundedRect(palette.tint, 16, palette.border, 1)
            pad(18, 0)
        }, MATCH, 56)
    }

    private fun save() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        when {
            name.isEmpty() -> toast("Wpisz swoje imię")
            email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> toast("Niepoprawny adres e-mail")
            else -> {
                settings.name = name
                settings.email = email
                toast("Zapisano zmiany")
                finish()
            }
        }
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}
