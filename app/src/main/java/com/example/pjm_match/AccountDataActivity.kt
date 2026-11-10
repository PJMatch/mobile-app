package com.example.pjm_match

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import com.example.pjm_match.ui.Fonts
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.dp
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.iconTile
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.oval
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical

class AccountDataActivity : BaseActivity() {
    override val tabRoot = Tab.PROFILE

    private val backgroundColor get() = color("#F0F0FA", "#12121E")
    private val surfaceColor get() = color("#FFFFFF", "#202031")
    private val borderColor get() = color("#DDDDF0", "#3A394F")
    private val textColor get() = color("#1A1A5E", "#EFEDFF")
    private val secondaryColor get() = color("#5A5AAA", "#AAA8C2")
    private val linkColor get() = color("#2D2D9F", "#B8ACFF")
    private val tintColor get() = color("#E8E8F8", "#302B4D")
    private val accentColor get() = color("#E07B00", "#FFB64D")
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText

    private fun color(light: String, dark: String) =
        (if (Palette.isDark) dark else light).toColorInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(backgroundColor)
        scaffold(Tab.PROFILE) {
            place(backNav())
            place(header(), top = 20)
            place(userSummaryCard(), top = 20)
            place(label("Twoje dane", 20f, textColor, 700), top = 20, bottom = 10)
            place(userDataCard())
            place(label("Bezpieczeństwo", 20f, textColor, 700), top = 20, bottom = 10)
            place(securityCard())
            place(infoNoteRow(), top = 18)
            place(saveButton(), MATCH, 52, top = 12)
        }.setBackgroundColor(backgroundColor)
        nameInput.setText(savedInstanceState?.getString("account_name") ?: AccountProfile.name(this))
        emailInput.setText(savedInstanceState?.getString("account_email") ?: AccountProfile.email(this))
    }

    private fun backNav(): LinearLayout = horizontal {
        minimumHeight = dp(32)
        place(icon(Icons.profileChevronLeft, linkColor), WRAP, WRAP, end = 10)
        place(label("Profil", 14f, linkColor, 700), WRAP, WRAP)
        onTap(16, Palette.ripple) { finish() }
    }

    private fun header(): LinearLayout = vertical {
        place(eyebrow("USTAWIENIA", accentColor))
        place(label("Dane konta", 30f, textColor, 800), top = 6)
        place(label("Zarządzaj swoimi danymi i zadbaj o bezpieczeństwo konta.", 14f, secondaryColor), top = 8)
    }

    private fun accountCard(padding: Int = 20, block: LinearLayout.() -> Unit) =
        card(padding = padding, background = surfaceColor, border = borderColor, block = block)

    private fun userSummaryCard(): LinearLayout = accountCard(padding = 16) {
        place(horizontal {
            place(FrameLayout(this@AccountDataActivity).apply {
                background = oval(tintColor)
                place(label(AccountProfile.initial(this@AccountDataActivity), 28f, linkColor, 800) {
                    gravity = Gravity.CENTER
                }, MATCH, MATCH)
            }, 60, 60, start = 4)
            place(vertical {
                place(label(AccountProfile.name(this@AccountDataActivity), 22f, textColor, 700))
                place(label("Początkująca · PJM", 12f, secondaryColor, 500), top = 2)
            }, 0, WRAP, 1f, start = 14)
        })
    }

    private fun userDataCard(): LinearLayout = accountCard {
        val nameLabel = place(label("Imię", 12f, textColor, 700))
        nameInput = inputBox(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS).apply {
            id = android.R.id.text1
        }
        nameLabel.labelFor = nameInput.id
        place(nameInput, MATCH, 48, top = 8)

        val emailLabel = place(label("Adres e-mail", 12f, textColor, 700), top = 16)
        emailInput = inputBox(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS).apply {
            id = android.R.id.text2
            hint = "Twój adres e-mail"
            imeOptions = EditorInfo.IME_ACTION_DONE
            setOnEditorActionListener { _, action, _ ->
                if (action == EditorInfo.IME_ACTION_DONE) { saveChanges(); true } else false
            }
        }
        emailLabel.labelFor = emailInput.id
        place(emailInput, MATCH, 48, top = 8)
    }

    private fun inputBox(type: Int): EditText = EditText(this).apply {
        setTextColor(textColor)
        setHintTextColor(secondaryColor)
        textSize = 14f
        inputType = type
        typeface = Fonts.weight(400)
        background = roundedRect(backgroundColor, 14, borderColor, 1)
        pad(14, 10)
        setSingleLine(true)
        imeOptions = EditorInfo.IME_ACTION_NEXT
    }

    private fun securityCard(): LinearLayout = accountCard(padding = 16) {
        place(horizontal {
            minimumHeight = dp(40)
            place(iconTile(Icons.profileLock, linkColor, tintColor, sizeDp = 34, radiusDp = 12), 34, 34)
            place(vertical {
                place(label("Zmień hasło", 14f, textColor, 700))
                place(label("••••••••", 14f, secondaryColor, 500), top = 2)
            }, 0, WRAP, 1f, start = 12)
            place(icon(Icons.profileChevronRight, secondaryColor), WRAP, WRAP, end = 4)
            onTap(16, Palette.ripple) { showPasswordInfo() }
        })
    }

    private fun showPasswordInfo() {
        val content = vertical {
            pad(24, 12)
            place(label("Logowanie w tej wersji aplikacji jest demonstracyjne. Hasło nie jest weryfikowane ani zapisywane. Zmiana hasła będzie dostępna po podłączeniu systemu kont.", 15f, textColor))
        }
        AlertDialog.Builder(this)
            .setTitle("Zmiana hasła")
            .setView(content)
            .setPositiveButton("Rozumiem", null)
            .create().apply {
                setOnShowListener {
                    window?.setBackgroundDrawable(roundedRect(surfaceColor, 24, borderColor, 1))
                    findViewById<android.widget.TextView>(androidx.appcompat.R.id.alertTitle)?.setTextColor(textColor)
                    getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(linkColor)
                }
            }.show()
    }

    private fun infoNoteRow(): LinearLayout = horizontal {
        place(icon(Icons.profileShield, accentColor), WRAP, WRAP, gravity = Gravity.TOP, top = 2, start = 4)
        place(label("Imię i e-mail zapisujemy tylko na tym urządzeniu. Zmiana e-maila do logowania będzie dostępna po podłączeniu systemu kont.", 12f, secondaryColor),
            0, WRAP, 1f, start = 12)
    }

    private fun saveButton(): ActionButton = primaryButton("Zapisz zmiany") { saveChanges() }.apply {
        background = roundedRect(color("#2D2D9F", "#5848BD"), 18,
            if (Palette.isDark) linkColor else Color.TRANSPARENT, if (Palette.isDark) 1 else 0)
    }

    private fun saveChanges() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        if (name.isBlank()) {
            nameInput.error = "Podaj imię"
            nameInput.requestFocus()
            return
        }
        if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Podaj poprawny adres e-mail"
            emailInput.requestFocus()
            return
        }
        AccountProfile.save(this, name, email)
        toast("Zapisano dane na tym urządzeniu")
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("account_name", nameInput.text.toString())
        outState.putString("account_email", emailInput.text.toString())
        super.onSaveInstanceState(outState)
    }
}

/** Local profile details, independent of a future authentication service. */
internal object AccountProfile {
    private fun prefs(context: android.content.Context) =
        context.getSharedPreferences("pjm_match_profile", android.content.Context.MODE_PRIVATE)

    fun name(context: android.content.Context): String =
        prefs(context).getString("name", "Ania") ?: "Ania"

    fun email(context: android.content.Context): String =
        prefs(context).getString("email", "") ?: ""

    fun initial(context: android.content.Context): String =
        name(context).take(1).uppercase(java.util.Locale.getDefault())

    fun save(context: android.content.Context, name: String, email: String) {
        prefs(context).edit().putString("name", name).putString("email", email).apply()
    }
}
