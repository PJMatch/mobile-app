package com.example.pjm_match

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.iconTile
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical

class AccountDataActivity : BaseActivity() {

    override val tabRoot = Tab.PROFILE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(Tab.PROFILE) {
            place(backNav())
            place(header(), top = 12)
            place(userSummaryCard(), top = 20)
            place(sectionTitle("Twoje dane"), top = 28, bottom = 14)
            place(userDataCard())
            place(sectionTitle("Bezpieczeństwo"), top = 28, bottom = 14)
            place(securityCard())
            place(infoNoteRow(), top = 18, bottom = 20)
            place(saveButton(), top = 8, bottom = 24)
        }
    }

    private fun backNav(): LinearLayout = horizontal {
        place(icon(Icons.profileChevronLeft, Palette.primary), WRAP, WRAP, end = 10)
        place(label("Profil", 16f, Palette.primary, 700), WRAP, WRAP)
        onTap(16, Palette.ripple) { finish() }
    }

    private fun header(): LinearLayout = vertical {
        place(eyebrow("USTAWIENIA", Palette.accent))
        place(screenTitle("Dane konta"), top = 6)
        place(bodyText("Zarządzaj swoimi danymi i zadbaj o bezpieczeństwo konta."), top = 8)
    }

    private fun userSummaryCard(): LinearLayout = card(padding = 20) {
        place(horizontal {
            place(avatar(LearnerStats.initial, 24f), 60, 60)
            place(vertical {
                place(label("Ania", 22f, Palette.text, 700))
                place(label("Początkująca · PJM", 14.5f, Palette.textSecondary, 500), top = 2)
            }, 0, WRAP, 1f, start = 16)
        })
    }

    private fun userDataCard(): LinearLayout = card(padding = 20) {
        place(label("Imię", 14f, Palette.text, 700))
        place(inputBox("Ania", InputType.TYPE_CLASS_TEXT), MATCH, 52, top = 8)

        place(label("Adres e-mail", 14f, Palette.text, 700), top = 18)
        place(inputBox("Twój adres e-mail", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, isHint = true), MATCH, 52, top = 8)
    }

    private fun inputBox(value: String, type: Int, isHint: Boolean = false): EditText = EditText(this).apply {
        if (isHint) {
            hint = value
            setHintTextColor(Palette.textSecondary)
        } else {
            setText(value)
        }
        setTextColor(Palette.text)
        textSize = 16f
        inputType = type
        background = roundedRect(Palette.tint, 16)
        pad(16, 12)
        setSingleLine(true)
    }

    private fun securityCard(): LinearLayout = card(padding = 16) {
        place(horizontal {
            place(iconTile(Icons.profileLock, Palette.primary, Palette.tint, sizeDp = 34, radiusDp = 12), 34, 34)
            place(vertical {
                place(label("Zmień hasło", 17f, Palette.text, 700))
                place(label("••••••••", 14f, Palette.textSecondary, 500), top = 2)
            }, 0, WRAP, 1f, start = 14)
            place(icon(Icons.profileChevronRight, Palette.textMuted), WRAP, WRAP)
            onTap(16, Palette.ripple) {
                Toast.makeText(this@AccountDataActivity, "Zmiana hasła w przygotowaniu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun infoNoteRow(): LinearLayout = horizontal {
        place(icon(Icons.profileShield, Palette.accent), WRAP, WRAP, gravity = Gravity.TOP, top = 2)
        place(
            label("Po zmianie adresu e-mail wyślemy na niego link potwierdzający.", 13.5f, Palette.textSecondary, 500),
            0, WRAP, 1f, start = 10,
        )
    }

    private fun saveButton(): ActionButton = primaryButton("Zapisz zmiany") {
        Toast.makeText(this, "Zapisano zmiany", Toast.LENGTH_SHORT).show()
        finish()
    }
}
