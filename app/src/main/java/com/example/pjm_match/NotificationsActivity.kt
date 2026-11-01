package com.example.pjm_match

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.ToggleSwitch
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.place

class NotificationsActivity : BaseActivity() {

    private lateinit var reminder: ToggleSwitch
    private lateinit var sounds: ToggleSwitch
    private lateinit var vibrations: ToggleSwitch
    private lateinit var messages: ToggleSwitch
    private lateinit var timeValue: TextView
    private lateinit var timeSubtitle: TextView
    private var reminderMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reminderMinute = settings.reminderMinuteOfDay
        reminder = toggle(settings.reminderEnabled)
        sounds = toggle(settings.soundsEnabled)
        vibrations = toggle(settings.vibrationsEnabled)
        messages = toggle(settings.messagesEnabled)

        scaffold(
            navTab = Tab.PROFILE,
            footer = { place(primaryButton("Zapisz zmiany") { save() }, MATCH, 56) },
        ) {
            settingsHeader(this, "Ustawienia powiadomień", "Ucz się we własnym rytmie. Wybierz, jak mamy Ci przypominać o nauce.")
            section(this@NotificationsActivity, "Przypomnienia o nauce", top = 0)
            place(card(padding = 0) {
                place(toggleRow(R.drawable.ic_bell, "Przypomnienie o nauce", "Pomaga utrzymać codzienną serię", reminder))
                rowDivider(this@NotificationsActivity)
                place(timeRow())
            })
            section(this@NotificationsActivity, "Dźwięki i wiadomości")
            place(card(padding = 0) {
                place(toggleRow(R.drawable.ic_volume, "Dźwięki", "Sygnały powiadomień", sounds))
                rowDivider(this@NotificationsActivity)
                place(toggleRow(R.drawable.ic_vibrate, "Wibracje", "Delikatne sygnały dotykowe", vibrations))
                rowDivider(this@NotificationsActivity)
                place(toggleRow(R.drawable.ic_message, "Wiadomości", "Nowości i wskazówki dotyczące nauki", messages))
            })
            place(infoNote(R.drawable.ic_info, "Powiadomienia możesz też zmienić w ustawieniach swojego telefonu."), top = 20)
        }
    }

    private fun toggle(on: Boolean) = ToggleSwitch(this, palette).apply { setOn(on) }

    private fun toggleRow(iconRes: Int, title: String, subtitle: String, toggle: ToggleSwitch): LinearLayout =
        settingsRow(iconRes, title, subtitle, toggle) { toggle.performClick() }

    private fun timeRow(): LinearLayout {
        timeValue = label("", 19f, palette.text, 700)
        timeSubtitle = settingsSubtitle("")
        updateTimeLabels()
        val trailing = horizontal {
            place(timeValue, WRAP, WRAP, end = 6)
            place(chevron(), 20, 20)
        }
        return settingsRow(R.drawable.ic_clock, "Godzina przypomnienia", timeSubtitle, trailing) { pickTime() }
    }

    private fun pickTime() {
        TimePickerDialog(this, { _, hour, minute ->
            reminderMinute = hour * 60 + minute
            updateTimeLabels()
        }, reminderMinute / 60, reminderMinute % 60, true).show()
    }

    private fun updateTimeLabels() {
        val time = AppSettings.formatTime(reminderMinute)
        timeValue.text = time
        timeSubtitle.text = "Codziennie o $time"
    }

    private fun save() {
        settings.reminderEnabled = reminder.isOn
        settings.reminderMinuteOfDay = reminderMinute
        settings.soundsEnabled = sounds.isOn
        settings.vibrationsEnabled = vibrations.isOn
        settings.messagesEnabled = messages.isOn
        Toast.makeText(this, "Zapisano ustawienia powiadomień", Toast.LENGTH_SHORT).show()
        finish()
    }
}
