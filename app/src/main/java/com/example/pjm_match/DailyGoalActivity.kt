package com.example.pjm_match

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.content.res.ColorStateList
import androidx.core.view.ViewCompat
import com.example.pjm_match.ui.*

class DailyGoalActivity : BaseActivity() {
    private var selectedGoal = 50
    private val choices = mutableListOf<Pair<Int, LinearLayout>>()
    private val radios = mutableListOf<Pair<Int, RadioButton>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedGoal = savedInstanceState?.getInt("selected_goal") ?: DailyGoalSettings.get(this)
        scaffold(Tab.PROFILE, footer = {
            place(primaryButton("Zapisz zmiany") {
                DailyGoalSettings.save(this@DailyGoalActivity, selectedGoal)
                finish()
            }, MATCH, 52)
        }) {
            place(horizontal {
                minimumHeight = dp(48)
                place(icon(Icons.profileChevronLeft, Palette.primary), WRAP, WRAP, end = 10)
                place(label("Profil", 16f, Palette.primary, 700), WRAP, WRAP)
                onTap(16, Palette.ripple) { finish() }
            })
            place(vertical {
                place(eyebrow("USTAWIENIA", Palette.accent))
                place(screenTitle("Cel dzienny"), top = 6)
                place(bodyText("Małe kroki, wielkie postępy. Dopasuj swój codzienny cel do własnego rytmu."), top = 8)
            }, top = 16)
            place(horizontal {
                background = roundedRect(Palette.tint, 18)
                pad(16, 12)
                place(icon(Icons.profileTarget, Palette.primary), WRAP, WRAP, end = 10)
                place(label("Twój obecny cel: ${DailyGoalSettings.get(this@DailyGoalActivity)} XP dziennie", 14f, Palette.primary, 500), 0, WRAP, 1f)
            }, top = 20)
            place(label("Wybierz swój cel", 18f, Palette.text, 700), top = 24, bottom = 16)
            val titles = listOf("Na spokojnie", "Regularnie", "Ambitnie", "Intensywnie")
            val descriptions = listOf("20 XP dziennie", "50 XP dziennie", "100 XP dziennie", "150 XP dziennie")
            DailyGoalSettings.options.forEachIndexed { index, xp ->
                place(goalOption(xp, titles[index], descriptions[index]), top = if (index == 0) 0 else 12)
            }
            place(label("Cel możesz zmienić w każdej chwili. Najważniejsza jest regularność!", 13f, Palette.textSecondary), top = 20)
        }
        updateSelection()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selected_goal", selectedGoal)
        super.onSaveInstanceState(outState)
    }

    private fun goalOption(xp: Int, title: String, description: String): LinearLayout = horizontal {
        minimumHeight = dp(84)
        pad(16, 16)
        place(label("$xp", 26f, Palette.primary, 700).apply {
            gravity = Gravity.CENTER
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }, 68, WRAP)
        place(vertical {
            place(label(title, 17f, Palette.text, 700))
            place(label(description, 14f, Palette.textSecondary), top = 3)
        }, 0, WRAP, 1f, start = 16, end = 10)
        val radio = RadioButton(context).apply {
            buttonTintList = ColorStateList.valueOf(Palette.primary)
            isClickable = false
            isFocusable = false
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
        radios += xp to radio
        place(radio, WRAP, WRAP)
        contentDescription = "$title, $description"
        ViewCompat.setAccessibilityDelegate(this, object : androidx.core.view.AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: androidx.core.view.accessibility.AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.className = RadioButton::class.java.name
                info.isCheckable = true
                info.isChecked = xp == selectedGoal
            }
        })
        choices += xp to this
        onTap(24, Palette.ripple) {
            selectedGoal = xp
            updateSelection()
        }
    }

    private fun updateSelection() {
        choices.forEach { (xp, row) ->
            row.background = roundedRect(
                if (xp == selectedGoal) Palette.tint else Palette.surface,
                24, if (xp == selectedGoal) Palette.primary else Palette.border,
                if (xp == selectedGoal) 1.5f else 1f,
            )
            row.isSelected = xp == selectedGoal
        }
        radios.forEach { (xp, radio) -> radio.isChecked = xp == selectedGoal }
    }
}
