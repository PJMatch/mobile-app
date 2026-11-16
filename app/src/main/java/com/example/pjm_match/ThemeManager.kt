package com.example.pjm_match

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.pjm_match.ui.Palette

enum class ThemeMode(val code: String, val title: String, val description: String) {
    LIGHT("light", "Jasny", "Jasne tło i fioletowe akcenty"),
    DARK("dark", "Ciemny", "Ciemne tło i łagodny kontrast"),
    SYSTEM("system", "Systemowy", "Zgodnie z ustawieniami telefonu")
}

object ThemeManager {
    private const val PREFS_NAME = "pjm_match_theme_prefs"
    private const val KEY_THEME_MODE = "key_theme_mode"

    fun getThemeMode(context: Context): ThemeMode {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val code = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.code) ?: ThemeMode.SYSTEM.code
        return ThemeMode.entries.firstOrNull { it.code == code } ?: ThemeMode.SYSTEM
    }

    fun setThemeMode(context: Context, mode: ThemeMode) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_THEME_MODE, mode.code).apply()
        applyTheme(context)
    }

    fun isDark(context: Context): Boolean {
        val mode = getThemeMode(context)
        return when (mode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> {
                val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    fun applyTheme(context: Context) {
        val dark = isDark(context)
        Palette.isDark = dark

        val nightMode = when (getThemeMode(context)) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }
}
