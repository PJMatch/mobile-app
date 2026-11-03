package com.example.pjm_match

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.pjm_match.ui.IconSpec
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.WRAP
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

class AppearanceActivity : BaseActivity() {

    private lateinit var selectedMode: ThemeMode
    private var optionsContainer: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedMode = ThemeManager.getThemeMode(this)

        scaffold(
            navTab = Tab.PROFILE,
            footer = {
                place(primaryButton("Zapisz zmiany") {
                    ThemeManager.setThemeMode(this@AppearanceActivity, selectedMode)
                    finish()
                })
            }
        ) {
            place(backHeader())
            place(header(), top = 16)
            place(sectionTitle("Motyw aplikacji"), top = 28, bottom = 14)
            optionsContainer = place(vertical())
            renderOptions()
            place(sectionTitle("Podgląd"), top = 28, bottom = 14)
            place(previewCard())
            place(disclaimerNote(), top = 16, bottom = 16)
        }
    }

    private fun backHeader(): LinearLayout = horizontal {
        pad(0, 4)
        place(icon(Icons.profileChevronLeft, Palette.primary), WRAP, WRAP, end = 10)
        place(label("Profil", 16f, Palette.primary, 700), WRAP, WRAP)
        onTap(16, Palette.ripple) { finish() }
    }

    private fun header(): LinearLayout = vertical {
        place(eyebrow("USTAWIENIA", Palette.accent))
        place(screenTitle("Wygląd"), top = 6)
        place(bodyText("Dopasuj wygląd aplikacji do siebie. Wybierz motyw, w którym uczysz się najlepiej."), top = 8)
    }

    private fun renderOptions() {
        val container = optionsContainer ?: return
        container.removeAllViews()

        ThemeMode.entries.forEachIndexed { index, mode ->
            val iconSpec = when (mode) {
                ThemeMode.LIGHT -> Icons.profileTheme
                ThemeMode.DARK -> Icons.profileTheme
                ThemeMode.SYSTEM -> Icons.profileTheme
            }
            container.place(
                themeOptionCard(mode, iconSpec, mode == selectedMode) {
                    selectedMode = mode
                    renderOptions()
                },
                top = if (index > 0) 12 else 0
            )
        }
    }

    private fun themeOptionCard(
        mode: ThemeMode,
        iconSpec: IconSpec,
        isSelected: Boolean,
        onClick: () -> Unit
    ): LinearLayout = horizontal {
        pad(18, 16)
        val borderColor = if (isSelected) Palette.primary else Palette.border
        val borderWidth = if (isSelected) 2 else 1
        background = roundedRect(Palette.surface, 20, borderColor, borderWidth)

        place(iconTile(iconSpec, Palette.primary, Palette.tint, sizeDp = 42, radiusDp = 14), 42, 42)
        place(vertical {
            place(label(mode.title, 18f, Palette.text, 700))
            place(label(mode.description, 14f, Palette.textSecondary, 500), top = 2)
        }, 0, WRAP, 1f, start = 14)

        place(radioCircle(isSelected), 24, 24)

        onTap(20, Palette.ripple, onClick)
    }

    private fun radioCircle(isSelected: Boolean): FrameLayout = FrameLayout(this).apply {
        background = oval(Palette.surface, if (isSelected) Palette.primary else Palette.border, if (isSelected) 2 else 1)
        if (isSelected) {
            val inner = View(this@AppearanceActivity).apply {
                background = oval(Palette.primary)
            }
            place(inner, 12, 12, gravity = Gravity.CENTER)
        }
    }

    private fun previewCard(): LinearLayout = card(padding = 20) {
        place(horizontal {
            place(iconTile(Icons.profileBook, Palette.primary, Palette.tint, sizeDp = 42, radiusDp = 14), 42, 42)
            place(vertical {
                place(label("Twoja nauka", 18f, Palette.text, 700))
                place(label("50 XP dziennie", 14f, Palette.textSecondary, 500), top = 2)
            }, 0, WRAP, 1f, start = 14)
            place(icon(Icons.pathFlame, Palette.accent), WRAP, WRAP)
        })
        place(progressBar(0.45f), MATCH, 8, top = 16)
    }

    private fun disclaimerNote(): LinearLayout = horizontal {
        gravity = Gravity.TOP
        place(icon(Icons.profileInfo, Palette.accent), WRAP, WRAP, top = 2, end = 10)
        place(label("Tryb systemowy automatycznie dopasowuje motyw do ustawień Twojego telefonu.", 13.5f, Palette.textSecondary), 0, WRAP, 1f)
    }
}
