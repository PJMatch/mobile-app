package com.example.pjm_match

import android.content.Context
import com.example.pjm_match.ui.ThemeMode

class AppSettings(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("pjmatch_settings", Context.MODE_PRIVATE)

    var loggedIn: Boolean
        get() = prefs.getBoolean(KEY_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply()

    var name: String
        get() = prefs.getString(KEY_NAME, null) ?: DEFAULT_NAME
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    var email: String
        get() = prefs.getString(KEY_EMAIL, null).orEmpty()
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var themeMode: ThemeMode
        get() = prefs.getString(KEY_THEME, null)
            ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
            ?: ThemeMode.SYSTEM
        set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

    var dailyGoalXp: Int
        get() = prefs.getInt(KEY_DAILY_GOAL, 50)
        set(value) = prefs.edit().putInt(KEY_DAILY_GOAL, value).apply()

    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER, true)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER, value).apply()

    var reminderMinuteOfDay: Int
        get() = prefs.getInt(KEY_REMINDER_TIME, 20 * 60)
        set(value) = prefs.edit().putInt(KEY_REMINDER_TIME, value).apply()

    var soundsEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUNDS, false)
        set(value) = prefs.edit().putBoolean(KEY_SOUNDS, value).apply()

    var vibrationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATIONS, value).apply()

    var messagesEnabled: Boolean
        get() = prefs.getBoolean(KEY_MESSAGES, true)
        set(value) = prefs.edit().putBoolean(KEY_MESSAGES, value).apply()

    val reminderTimeLabel: String
        get() = formatTime(reminderMinuteOfDay)

    val initial: String
        get() = name.trim().firstOrNull()?.uppercase() ?: "?"

    companion object {
        private const val DEFAULT_NAME = "Ania"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
        private const val KEY_THEME = "theme_mode"
        private const val KEY_DAILY_GOAL = "daily_goal_xp"
        private const val KEY_REMINDER = "reminder_enabled"
        private const val KEY_REMINDER_TIME = "reminder_minute_of_day"
        private const val KEY_SOUNDS = "sounds_enabled"
        private const val KEY_VIBRATIONS = "vibrations_enabled"
        private const val KEY_MESSAGES = "messages_enabled"

        fun formatTime(minuteOfDay: Int) = "%02d:%02d".format(minuteOfDay / 60, minuteOfDay % 60)
    }
}
