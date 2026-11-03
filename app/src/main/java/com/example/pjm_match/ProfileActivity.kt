package com.example.pjm_match

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.pjm_match.ui.IconSpec
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.dp
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.iconTile
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical

class ProfileActivity : BaseActivity() {

    override val tabRoot = Tab.PROFILE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(Tab.PROFILE) {
            place(header())
            place(userProfileCard(), top = 24)
            place(sectionTitle("Twoja nauka"), top = 28, bottom = 14)
            place(learningCard())
            place(sectionTitle("Konto"), top = 28, bottom = 14)
            place(accountCard())
            place(logoutButton(), top = 24, bottom = 16)
        }
    }

    private fun header(): LinearLayout = vertical {
        place(eyebrow("TWOJE KONTO", Palette.accent))
        place(screenTitle("Profil"), top = 6)
    }

    private fun userProfileCard(): LinearLayout = card(padding = 20) {
        place(horizontal {
            place(avatar(LearnerStats.initial, 24f), 60, 60)
            place(vertical {
                place(label("Ania", 22f, Palette.text, 700))
                place(label("Początkująca · PJM", 14.5f, Palette.textSecondary, 500), top = 2)
            }, 0, WRAP, 1f, start = 16)
            place(label("Edytuj", 15f, Palette.primary, 700), WRAP, WRAP)
        })

        place(View(this@ProfileActivity).apply {
            setBackgroundColor(Palette.border)
        }, MATCH, 1, top = 18, bottom = 18)

        place(horizontal {
            place(statCol(Icons.startFlame, Palette.accent, "${LearnerStats.streakDays} dni", "Seria"), 0, WRAP, 1f)
            place(statCol(Icons.startZap, Palette.primary, "${LearnerStats.totalXp}", "Łącznie XP"), 0, WRAP, 1f)
            place(statCol(Icons.profileBook, Palette.primary, "${Curriculum.currentTopic.completedLessons}", "Lekcje"), 0, WRAP, 1f)
        })
    }

    private fun statCol(iconSpec: IconSpec, iconColor: Int, value: String, caption: String): LinearLayout = vertical {
        gravity = Gravity.CENTER_HORIZONTAL
        place(horizontal {
            place(icon(iconSpec, iconColor), WRAP, WRAP, end = 6)
            place(label(value, 18f, Palette.text, 700), WRAP, WRAP)
        })
        place(label(caption, 13f, Palette.textSecondary, 500), top = 4)
    }

    private fun learningCard(): LinearLayout = card(padding = 0) {
        place(profileOptionRow(Icons.profileTarget, "Cel dzienny", "50 XP dziennie"))
        place(divider())
        val mode = ThemeManager.getThemeMode(this@ProfileActivity)
        val subtitle = when (mode) {
            ThemeMode.LIGHT -> "Jasny"
            ThemeMode.DARK -> "Ciemny"
            ThemeMode.SYSTEM -> "Jasny · Automatycznie"
        }
        place(profileOptionRow(Icons.profileTheme, "Tryb jasny/ciemny", subtitle) {
            startActivity(Intent(this@ProfileActivity, AppearanceActivity::class.java))
        })
        place(divider())
        place(profileOptionRowWithSwitch(Icons.profileBell, "Przypomnienie o nauce", "Codziennie o 20:00", isChecked = true))
    }

    private fun accountCard(): LinearLayout = card(padding = 0) {
        place(profileOptionRow(Icons.profileUser, "Dane konta", "Adres e-mail i hasło") {
            startActivity(Intent(this@ProfileActivity, AccountDataActivity::class.java))
        })
        place(divider())
        place(profileOptionRow(Icons.profileShield, "Ustawienia powiadomień", "Dźwięki, wibracje i wiadomości") {
            startActivity(Intent(this@ProfileActivity, NotificationSettingsActivity::class.java))
        })
    }

    private fun profileOptionRow(
        iconSpec: IconSpec,
        title: String,
        subtitle: String,
        onClick: (() -> Unit)? = null,
    ): LinearLayout = horizontal {
        pad(16, 14)
        place(iconTile(iconSpec, Palette.primary, Palette.tint, sizeDp = 34, radiusDp = 12), 34, 34)
        place(vertical {
            place(label(title, 17f, Palette.text, 700))
            place(label(subtitle, 14f, Palette.textSecondary, 500), top = 2)
        }, 0, WRAP, 1f, start = 14)
        place(icon(Icons.profileChevronRight, Palette.textMuted), WRAP, WRAP)
        if (onClick != null) {
            onTap(20, Palette.ripple, onClick)
        }
    }

    private fun profileOptionRowWithSwitch(iconSpec: IconSpec, title: String, subtitle: String, isChecked: Boolean): LinearLayout = horizontal {
        pad(16, 14)
        place(iconTile(iconSpec, Palette.primary, Palette.tint, sizeDp = 34, radiusDp = 12), 34, 34)
        place(vertical {
            place(label(title, 17f, Palette.text, 700))
            place(label(subtitle, 14f, Palette.textSecondary, 500), top = 2)
        }, 0, WRAP, 1f, start = 14)
        place(switchControl(isChecked), 40, 24)
    }

    private fun switchControl(checked: Boolean): FrameLayout = FrameLayout(this).apply {
        background = roundedRect(if (checked) Palette.primary else Palette.border, 12)
        val thumb = View(this@ProfileActivity).apply {
            background = roundedRect(0xFFFFFFFF.toInt(), 9)
        }
        val p = FrameLayout.LayoutParams(this@ProfileActivity.dp(18), this@ProfileActivity.dp(18)).apply {
            gravity = if (checked) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.START or Gravity.CENTER_VERTICAL
            setMargins(this@ProfileActivity.dp(3), 0, this@ProfileActivity.dp(3), 0)
        }
        addView(thumb, p)
    }

    private fun logoutButton(): LinearLayout = horizontal {
        gravity = Gravity.CENTER
        background = roundedRect(Palette.surface, 18, Palette.border, 1)
        minimumHeight = dp(52)
        pad(16, 12)
        place(icon(Icons.profileLogout, Palette.primary), WRAP, WRAP)
        place(label("Wyloguj się", 16f, Palette.primary, 700), WRAP, WRAP, start = 10)
    }

    private fun divider(): View = View(this).apply {
        setBackgroundColor(Palette.border)
    }.apply {
        layoutParams = LinearLayout.LayoutParams(MATCH, dp(1)).apply {
            setMargins(dp(16), 0, dp(16), 0)
        }
    }
}
