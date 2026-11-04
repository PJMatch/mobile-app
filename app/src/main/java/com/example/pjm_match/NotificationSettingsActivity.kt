package com.example.pjm_match

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
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

class NotificationSettingsActivity : BaseActivity() {

    override val tabRoot = Tab.PROFILE

    private var remindersContainer: LinearLayout? = null
    private var soundsContainer: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(Tab.PROFILE) {
            place(backNav())
            place(header(), top = 12)
            place(sectionTitle("Przypomnienia o nauce"), top = 28, bottom = 14)
            remindersContainer = place(vertical())
            renderReminders()
            place(sectionTitle("Dźwięki i wiadomości"), top = 28, bottom = 14)
            soundsContainer = place(vertical())
            renderSounds()
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
        place(screenTitle("Ustawienia powiadomień"), top = 6)
        place(bodyText("Ucz się we własnym rytmie. Wybierz, jak mamy Ci przypominać o nauce."), top = 8)
    }

    private fun renderReminders() {
        val container = remindersContainer ?: return
        container.removeAllViews()
        container.place(card(padding = 0) {
            val reminderEnabled = SettingsManager.isReminderEnabled(this@NotificationSettingsActivity)
            place(profileOptionRowWithSwitch(Icons.profileBell, "Przypomnienie o nauce", "Pomaga utrzymać codzienną serię", reminderEnabled) {
                val newState = !SettingsManager.isReminderEnabled(this@NotificationSettingsActivity)
                SettingsManager.setReminderEnabled(this@NotificationSettingsActivity, newState)
                SettingsManager.scheduleDailyReminder(this@NotificationSettingsActivity)
                renderReminders()
            })
            place(divider())
            val h = SettingsManager.getReminderHour(this@NotificationSettingsActivity)
            val m = SettingsManager.getReminderMinute(this@NotificationSettingsActivity)
            val timeString = String.format(java.util.Locale.getDefault(), "%02d:%02d", h, m)
            place(profileOptionRowWithValue(Icons.profileClock, "Godzina przypomnienia", "Codziennie o $timeString", value = timeString) {
                val picker = android.app.TimePickerDialog(
                    this@NotificationSettingsActivity,
                    { _, hourOfDay, minute ->
                        SettingsManager.setReminderTime(this@NotificationSettingsActivity, hourOfDay, minute)
                        SettingsManager.scheduleDailyReminder(this@NotificationSettingsActivity)
                        renderReminders()
                    },
                    h, m, true
                )
                picker.show()
            })
        })
    }

    private fun renderSounds() {
        val container = soundsContainer ?: return
        container.removeAllViews()
        container.place(card(padding = 0) {
            place(profileOptionRowWithSwitch(Icons.profileSound, "Dźwięki", "Sygnały powiadomień", SettingsManager.isSoundEnabled(this@NotificationSettingsActivity)) {
                val newState = !SettingsManager.isSoundEnabled(this@NotificationSettingsActivity)
                SettingsManager.setSoundEnabled(this@NotificationSettingsActivity, newState)
                renderSounds()
            })
            place(divider())
            place(profileOptionRowWithSwitch(Icons.profileVibrate, "Wibracje", "Delikatne sygnały dotykowe", SettingsManager.isVibrationEnabled(this@NotificationSettingsActivity)) {
                val newState = !SettingsManager.isVibrationEnabled(this@NotificationSettingsActivity)
                SettingsManager.setVibrationEnabled(this@NotificationSettingsActivity, newState)
                renderSounds()
            })
            place(divider())
            place(profileOptionRowWithSwitch(Icons.profileMessages, "Wiadomości", "Nowości i wskazówki dotyczące nauki", SettingsManager.isMessagesEnabled(this@NotificationSettingsActivity)) {
                val newState = !SettingsManager.isMessagesEnabled(this@NotificationSettingsActivity)
                SettingsManager.setMessagesEnabled(this@NotificationSettingsActivity, newState)
                renderSounds()
            })
        })
    }

    private fun profileOptionRowWithSwitch(iconSpec: IconSpec, title: String, subtitle: String, isChecked: Boolean, onClick: (() -> Unit)? = null): LinearLayout = horizontal {
        pad(16, 14)
        place(iconTile(iconSpec, Palette.primary, Palette.tint, sizeDp = 34, radiusDp = 12), 34, 34)
        place(vertical {
            place(label(title, 17f, Palette.text, 700))
            place(label(subtitle, 14f, Palette.textSecondary, 500), top = 2)
        }, 0, WRAP, 1f, start = 14)
        place(switchControl(isChecked), 40, 24)
        if (onClick != null) {
            onTap(20, Palette.ripple, onClick)
        }
    }

    private fun profileOptionRowWithValue(iconSpec: IconSpec, title: String, subtitle: String, value: String, onClick: (() -> Unit)? = null): LinearLayout = horizontal {
        pad(16, 14)
        place(iconTile(iconSpec, Palette.primary, Palette.tint, sizeDp = 34, radiusDp = 12), 34, 34)
        place(vertical {
            place(label(title, 17f, Palette.text, 700))
            place(label(subtitle, 14f, Palette.textSecondary, 500), top = 2)
        }, 0, WRAP, 1f, start = 14)
        place(horizontal {
            place(label(value, 16f, Palette.primary, 700), WRAP, WRAP, end = 6)
            place(icon(Icons.profileChevronRight, Palette.textMuted), WRAP, WRAP)
        }, WRAP, WRAP)
        if (onClick != null) {
            onTap(20, Palette.ripple, onClick)
        }
    }

    private fun switchControl(checked: Boolean): FrameLayout = FrameLayout(this).apply {
        background = roundedRect(if (checked) Palette.primary else Palette.border, 12)
        val thumb = View(this@NotificationSettingsActivity).apply {
            background = roundedRect(0xFFFFFFFF.toInt(), 9)
        }
        val p = FrameLayout.LayoutParams(this@NotificationSettingsActivity.dp(18), this@NotificationSettingsActivity.dp(18)).apply {
            gravity = if (checked) Gravity.END or Gravity.CENTER_VERTICAL else Gravity.START or Gravity.CENTER_VERTICAL
            setMargins(this@NotificationSettingsActivity.dp(3), 0, this@NotificationSettingsActivity.dp(3), 0)
        }
        addView(thumb, p)
    }

    private fun infoNoteRow(): LinearLayout = horizontal {
        place(icon(Icons.profileInfo, Palette.accent), WRAP, WRAP, gravity = Gravity.TOP, top = 2)
        place(
            label("Powiadomienia możesz też zmienić w ustawieniach swojego telefonu.", 13.5f, Palette.textSecondary, 500),
            0, WRAP, 1f, start = 10,
        )
    }

    private fun saveButton(): ActionButton = primaryButton("Zapisz zmiany") {
        Toast.makeText(this, "Zapisano ustawienia", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun divider(): View = View(this).apply {
        setBackgroundColor(Palette.border)
    }.apply {
        layoutParams = LinearLayout.LayoutParams(MATCH, dp(1)).apply {
            setMargins(dp(16), 0, dp(16), 0)
        }
    }
}
