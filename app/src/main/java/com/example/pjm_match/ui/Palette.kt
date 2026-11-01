package com.example.pjm_match.ui

import android.graphics.Color

object Palette {
    val background = c("#F0F0FA")
    val surface = c("#FFFFFF")
    val text = c("#1A1A5E")
    val textSecondary = c("#5A5AAA")
    val textMuted = c("#9898C0")
    val primary = c("#2D2D9F")
    val onPrimary = c("#FFFFFF")
    val tint = c("#E8E8F8")
    val border = c("#DDDDF0")
    val accent = c("#E07B00")
    val accentStrong = c("#F5A623")
    val accentTint = c("#FFF3E0")
    val hero = c("#2B2B8C")
    val heroIcon = c("#3D3DB5")
    val heroSubtext = c("#C0C0F0")
    val heroTrack = c("#1E1E7A")
    val heroButtonText = c("#1E1E7A")
    val locked = c("#C4C4F3")
    val lockedPath = c("#D0D0F0")
    val ripple = c("#292D2D9F")
    val overlay = c("#E61A1A5E")

    private fun c(hex: String) = Color.parseColor(hex)
}
