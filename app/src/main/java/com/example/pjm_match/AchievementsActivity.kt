package com.example.pjm_match

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import com.example.pjm_match.ui.*

class AchievementsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(navTab = null) {
            place(horizontal {
                minimumHeight = dp(48)
                place(icon(Icons.profileChevronLeft, Palette.primary), WRAP, WRAP, end = 10)
                place(label("Statystyki", 16f, Palette.primary, 700), WRAP, WRAP)
                onTap(16, Palette.ripple) { finish() }
            })
            place(eyebrow("MAŁE POWODY DO DUMY"), top = 20)
            place(screenTitle("Osiągnięcia"), top = 6)
            place(pill("Zdobyte: ${Achievements.countLabel}", Palette.primary, Palette.tint), WRAP, WRAP, top = 20)
            Achievements.all.forEach { achievement ->
                place(achievementCard(achievement), top = 12)
            }
        }
    }

    private fun achievementCard(achievement: Achievement): LinearLayout = card(
        padding = 16,
        border = if (achievement.isUnlocked) Palette.primary else Palette.border,
    ) {
        contentDescription = "${achievement.title}. ${achievement.requirement} " +
            if (achievement.isUnlocked) "Zdobyta" else "Zablokowana"
        if (!achievement.isUnlocked) alpha = 0.6f
        place(horizontal {
            gravity = Gravity.CENTER_VERTICAL
            place(iconTile(
                if (achievement.isUnlocked) achievement.icon else Icons.pathLock,
                if (achievement.isUnlocked) Palette.primary else Palette.textMuted,
                Palette.tint,
            ), 52, 52, end = 14)
            place(vertical {
                place(label(achievement.title, 18f, Palette.text, 700))
                place(label(achievement.requirement, 14f, Palette.textSecondary), top = 4)
            }, 0, WRAP, 1f)
        })
    }
}
