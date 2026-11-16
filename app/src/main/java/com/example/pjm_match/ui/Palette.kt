package com.example.pjm_match.ui

import android.graphics.Color

object Palette {
    var isDark: Boolean = false

    val background get() = if (isDark) c("#0F0E17") else c("#F0F0FA")
    val surface get() = if (isDark) c("#1C1A2B") else c("#FFFFFF")
    val text get() = if (isDark) c("#FFFFFF") else c("#1A1A5E")
    val textSecondary get() = if (isDark) c("#9A95B5") else c("#5A5AAA")
    val textMuted get() = if (isDark) c("#656180") else c("#9898C0")
    val primary get() = if (isDark) c("#5B4BD8") else c("#2D2D9F")
    val onPrimary get() = if (isDark) c("#FFFFFF") else c("#FFFFFF")
    val tint get() = if (isDark) c("#27243A") else c("#E8E8F8")
    val border get() = if (isDark) c("#2E2A45") else c("#DDDDF0")
    val accent get() = if (isDark) c("#F59E0B") else c("#E07B00")
    val accentStrong get() = if (isDark) c("#FFB300") else c("#F5A623")
    val accentTint get() = if (isDark) c("#382B17") else c("#FFF3E0")
    val hero get() = if (isDark) c("#3B319E") else c("#2B2B8C")
    val heroIcon get() = if (isDark) c("#483CA6") else c("#3D3DB5")
    val heroSubtext get() = if (isDark) c("#B4ACE3") else c("#C0C0F0")
    val heroTrack get() = if (isDark) c("#221C59") else c("#1E1E7A")
    val heroButtonText get() = if (isDark) c("#FFFFFF") else c("#1E1E7A")
    val locked get() = if (isDark) c("#2F2B47") else c("#C4C4F3")
    val lockedPath get() = if (isDark) c("#383354") else c("#D0D0F0")
    val ripple get() = if (isDark) c("#335B4CDA") else c("#292D2D9F")
    val overlay get() = if (isDark) c("#E60F0E17") else c("#E61A1A5E")

    private fun c(hex: String) = Color.parseColor(hex)
}
