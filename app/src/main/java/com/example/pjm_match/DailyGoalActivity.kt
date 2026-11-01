package com.example.pjm_match

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.RadioIndicator
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical

class DailyGoalActivity : BaseActivity() {

    private data class GoalOption(val xp: Int, val title: String, val description: String)

    private val options = listOf(
        GoalOption(20, "Spokojnie", "Mały krok każdego dnia"),
        GoalOption(50, "Regularnie", "Dobry rytm codziennej nauki"),
        GoalOption(100, "Intensywnie", "Więcej czasu na nowe znaki"),
        GoalOption(150, "Ambitnie", "Duża porcja nauki każdego dnia"),
    )

    private var selectedXp = 0
    private val rows = mutableListOf<Triple<GoalOption, LinearLayout, RadioIndicator>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedXp = savedInstanceState?.getInt(KEY_SELECTED) ?: settings.dailyGoalXp
        scaffold(
            navTab = Tab.PROFILE,
            footer = { place(primaryButton("Zapisz zmiany") { save() }, MATCH, 56) },
        ) {
            settingsHeader(this, "Cel dzienny", "Wybierz swój rytm nauki PJM. Każdy zdobyty punkt przybliża Cię do celu.")
            place(horizontal {
                background = roundedRect(palette.tint, 18)
                pad(18, 14)
                place(icon(R.drawable.ic_target, palette.primary), 22, 22, end = 12)
                place(label("Twój obecny cel: ${settings.dailyGoalXp} XP dziennie", 15f, palette.text, 700), 0, WRAP, 1f)
            })
            section(this@DailyGoalActivity, "Ile XP dziennie?", top = 24)
            options.forEachIndexed { i, option -> place(optionRow(option), top = if (i == 0) 0 else 14) }
            place(infoNote(R.drawable.ic_flame, "Liczy się regularność. Cel możesz zmienić w każdej chwili, nie tracąc swojej serii."), top = 20)
        }
        refresh()
    }

    private fun optionRow(option: GoalOption): LinearLayout {
        val radio = RadioIndicator(this, palette)
        val row = horizontal {
            pad(20, 20)
            place(vertical {
                place(label("${option.xp} XP", 24f, palette.primary, 700) { maxLines = 1 })
                place(label("dziennie", 13f, palette.textSecondary), top = 2)
            }, 104, WRAP)
            place(vertical {
                place(label(option.title, 18f, palette.text, 700))
                place(label(option.description, 14f, palette.textSecondary), top = 4)
            }, 0, WRAP, 1f, end = 12)
            place(radio, WRAP, WRAP, gravity = Gravity.CENTER_VERTICAL)
            onTap(24, palette.ripple) {
                selectedXp = option.xp
                refresh()
            }
        }
        rows += Triple(option, row, radio)
        return row
    }

    private fun refresh() {
        rows.forEach { (option, row, radio) ->
            val selected = option.xp == selectedXp
            row.background = selectableBackground(selected)
            radio.checked = selected
        }
    }

    private fun save() {
        settings.dailyGoalXp = selectedXp
        Toast.makeText(this, "Zapisano cel: $selectedXp XP dziennie", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED, selectedXp)
    }

    private companion object {
        const val KEY_SELECTED = "selected_xp"
    }
}
