package com.example.pjm_match

import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.ProgressBarView
import com.example.pjm_match.ui.RadioIndicator
import com.example.pjm_match.ui.ThemeMode
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

class AppearanceActivity : BaseActivity() {

    private data class ThemeOption(val mode: ThemeMode, val title: String, val description: String, val iconRes: Int)

    private val options = listOf(
        ThemeOption(ThemeMode.LIGHT, "Jasny", "Jasne tło i fioletowe akcenty", R.drawable.ic_sun),
        ThemeOption(ThemeMode.DARK, "Ciemny", "Ciemne tło i łagodny kontrast", R.drawable.ic_moon),
        ThemeOption(ThemeMode.SYSTEM, "Systemowy", "Zgodnie z ustawieniami telefonu", R.drawable.ic_monitor_phone),
    )

    private lateinit var selected: ThemeMode
    private lateinit var previewHolder: FrameLayout
    private val rows = mutableListOf<Pair<ThemeOption, LinearLayout>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selected = savedInstanceState?.getString(KEY_SELECTED)?.let { ThemeMode.valueOf(it) } ?: settings.themeMode
        scaffold(
            navTab = Tab.PROFILE,
            footer = { place(primaryButton("Zapisz zmiany") { save() }, MATCH, 56) },
        ) {
            settingsHeader(this, "Wygląd", "Dopasuj wygląd aplikacji do siebie. Wybierz motyw, w którym uczysz się najlepiej.")
            section(this@AppearanceActivity, "Motyw aplikacji", top = 0)
            options.forEachIndexed { i, option -> place(optionRow(option), top = if (i == 0) 0 else 14) }
            section(this@AppearanceActivity, "Podgląd")
            previewHolder = place(FrameLayout(context))
            place(infoNote(R.drawable.ic_info, "Tryb systemowy automatycznie dopasowuje motyw do ustawień Twojego telefonu."), top = 20)
        }
        refresh()
    }

    private fun optionRow(option: ThemeOption): LinearLayout = horizontal {
        pad(20, 20)
        rows += option to this
        onTap(24, palette.ripple) {
            selected = option.mode
            refresh()
        }
    }

    private fun refresh() {
        rows.forEach { (option, row) ->
            val isSelected = option.mode == selected
            row.removeAllViews()
            row.background = selectableBackground(isSelected)
            row.place(iconTile(option.iconRes, palette.primary, if (isSelected) palette.surface else palette.tint), 44, 44)
            row.place(vertical {
                place(label(option.title, 18f, palette.text, 700))
                place(label(option.description, 14f, palette.textSecondary), top = 3)
            }, 0, WRAP, 1f, start = 16, end = 12)
            row.place(RadioIndicator(this, palette).apply { checked = isSelected }, WRAP, WRAP)
        }
        previewHolder.removeAllViews()
        previewHolder.place(preview(Palette.resolve(applicationContext, selected)), MATCH, WRAP)
    }

    private fun preview(p: Palette): LinearLayout = vertical {
        background = roundedRect(p.surface, 24, p.border, 1)
        pad(22, 20)
        place(horizontal {
            place(iconTile(R.drawable.ic_book_check, p.text, p.tint, sizeDp = 48, iconSizeDp = 24), 48, 48)
            place(vertical {
                place(label("Twoja nauka", 18f, p.text, 700))
                place(label("${settings.dailyGoalXp} XP dziennie", 14f, p.textSecondary), top = 3)
            }, 0, WRAP, 1f, start = 16)
            place(icon(R.drawable.ic_flame, p.accent), 24, 24, gravity = Gravity.TOP)
        })
        place(ProgressBarView(context, p.progressFill(), p.track).apply { fraction = 0.4f }, MATCH, 10, top = 20)
    }

    private fun save() {
        settings.themeMode = selected
        recreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SELECTED, selected.name)
    }

    private companion object {
        const val KEY_SELECTED = "selected_theme"
    }
}
