package com.example.pjm_match

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.ThemeMode
import com.example.pjm_match.ui.ToggleSwitch
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
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
        render()
    }

    override fun onRestart() {
        super.onRestart()
        render()
    }

    private fun render() {
        scaffold(Tab.PROFILE) {
            place(eyebrow("Twoje konto"))
            place(screenTitle("Profil"), top = 6)
            place(identityCard(), top = 24)
            section(this@ProfileActivity, "Twoja nauka")
            place(learningCard())
            section(this@ProfileActivity, "Konto")
            place(accountCard())
            place(logoutButton(), MATCH, 60, top = 28)
        }
    }

    private fun identityCard(): LinearLayout = card(padding = 22) {
        place(horizontal {
            place(avatar(80, 34f), 80, 80)
            place(vertical {
                place(label(settings.name, 28f, palette.text, 700) { maxLines = 1 })
                place(label(LearnerStats.level, 15.5f, palette.textSecondary), top = 4)
            }, 0, WRAP, 1f, start = 18)
            place(label("Edytuj", 16f, palette.primary, 700) {
                pad(6, 4)
                onTap(8, palette.ripple) { open(AccountActivity::class.java) }
            }, WRAP, WRAP, gravity = Gravity.TOP)
        })
        place(divider(), MATCH, 1, top = 22, bottom = 18)
        place(horizontal {
            place(profileStat(R.drawable.ic_flame, palette.accent, "${LearnerStats.streakDays} dni", "Seria"), 0, WRAP, 1f)
            place(profileStat(R.drawable.ic_zap, palette.primary, "${LearnerStats.totalXp}", "Łącznie XP"), 0, WRAP, 1f)
            place(profileStat(R.drawable.ic_book_check, palette.primary, "${Curriculum.completedLessons}", "Lekcje"), 0, WRAP, 1f)
        })
    }

    private fun profileStat(iconRes: Int, iconColor: Int, value: String, caption: String) = vertical {
        gravity = Gravity.CENTER_HORIZONTAL
        place(horizontal {
            place(icon(iconRes, iconColor), 20, 20, end = 6)
            place(label(value, 21f, palette.text, 700), WRAP, WRAP)
        }, WRAP, WRAP)
        place(label(caption, 13.5f, palette.textSecondary), WRAP, WRAP, top = 4)
    }

    private fun learningCard(): LinearLayout = card(padding = 0) {
        place(settingsRow(R.drawable.ic_target, "Cel dzienny", "${settings.dailyGoalXp} XP dziennie", chevron()) {
            open(DailyGoalActivity::class.java)
        })
        rowDivider(this@ProfileActivity)
        place(settingsRow(R.drawable.ic_sun, "Tryb jasny/ciemny", themeSummary(), chevron()) {
            open(AppearanceActivity::class.java)
        })
        rowDivider(this@ProfileActivity)
        val toggle = ToggleSwitch(context, palette).apply {
            setOn(settings.reminderEnabled)
            onChange = { settings.reminderEnabled = it }
        }
        place(settingsRow(R.drawable.ic_bell, "Przypomnienie o nauce", "Codziennie o ${settings.reminderTimeLabel}", toggle) {
            toggle.performClick()
        })
    }

    private fun accountCard(): LinearLayout = card(padding = 0) {
        place(settingsRow(R.drawable.ic_user, "Dane konta", "Adres e-mail i hasło", chevron()) {
            open(AccountActivity::class.java)
        })
        rowDivider(this@ProfileActivity)
        place(settingsRow(R.drawable.ic_shield_check, "Ustawienia powiadomień", "Dźwięki, wibracje i wiadomości", chevron()) {
            open(NotificationsActivity::class.java)
        })
    }

    private fun logoutButton() = ActionButton(this, palette.primary).apply {
        background = roundedRect(palette.tint, 20, palette.border, 1)
        setContent("Wyloguj się", R.drawable.ic_log_out, iconFirst = true)
        onTap(20, palette.ripple) {
            settings.loggedIn = false
            startActivity(Intent(this@ProfileActivity, LoginActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ))
        }
    }

    private fun themeSummary(): String = when (settings.themeMode) {
        ThemeMode.LIGHT -> "Jasny"
        ThemeMode.DARK -> "Ciemny"
        ThemeMode.SYSTEM -> (if (Palette.isSystemDark(applicationContext)) "Ciemny" else "Jasny") + " · Automatycznie"
    }

    private fun open(target: Class<*>) = startActivity(Intent(this, target))
}
