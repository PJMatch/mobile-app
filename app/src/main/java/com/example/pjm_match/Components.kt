package com.example.pjm_match

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.ProgressBarView
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.dp
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

class ActionButton(context: Context, private val textColor: Int) : LinearLayout(context) {
    private val iconStart: ImageView = context.icon(0, textColor)
    private val iconEnd: ImageView = context.icon(0, textColor)
    private val text: TextView = context.label("", 17f, textColor, 700) { maxLines = 1 }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        pad(20, 0)
        place(iconStart, 22, 22, end = 12)
        place(text, WRAP, WRAP)
        place(iconEnd, 22, 22, start = 12)
    }

    fun setContent(label: String, iconRes: Int? = null, iconFirst: Boolean = false) {
        text.text = label
        iconStart.visibility = View.GONE
        iconEnd.visibility = View.GONE
        if (iconRes != null) {
            val target = if (iconFirst) iconStart else iconEnd
            target.setImageResource(iconRes)
            target.visibility = View.VISIBLE
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1f else 0.55f
    }
}

fun BaseActivity.eyebrow(text: String, color: Int = palette.accent): TextView =
    label(text.uppercase(), 12.5f, color, 700) { letterSpacing = 0.04f }

fun BaseActivity.screenTitle(text: String, color: Int = palette.text): TextView =
    label(text, 34f, color, 800) { letterSpacing = -0.01f }

fun BaseActivity.bodyText(text: String, color: Int = palette.textSecondary, size: Float = 16f): TextView =
    label(text, size, color, 400)

fun BaseActivity.sectionTitle(text: String): TextView = label(text, 22f, palette.text, 700)

fun BaseActivity.card(
    radius: Int = 24,
    padding: Int = 20,
    background: Int = palette.surface,
    border: Int = palette.border,
    block: LinearLayout.() -> Unit = {},
): LinearLayout = vertical {
    this.background = roundedRect(background, radius, border, if (border == Color.TRANSPARENT) 0 else 1)
    pad(padding)
    block()
}

fun BaseActivity.selectableBackground(selected: Boolean, radius: Int = 24) =
    if (selected) roundedRect(palette.tint, radius, palette.primary, 2)
    else roundedRect(palette.surface, radius, palette.border, 1)

fun BaseActivity.primaryButton(text: String, iconRes: Int? = null, iconFirst: Boolean = false, onClick: () -> Unit): ActionButton =
    ActionButton(this, palette.onButton).apply {
        background = roundedRect(palette.button, 18, palette.buttonBorder, if (palette.isDark) 1 else 0)
        setContent(text, iconRes, iconFirst)
        onTap(18, 0x33FFFFFF) { if (isEnabled) onClick() }
    }

fun BaseActivity.secondaryButton(text: String, iconRes: Int? = null, iconFirst: Boolean = true, onClick: () -> Unit): ActionButton =
    ActionButton(this, palette.onSecondaryButton).apply {
        background = roundedRect(palette.secondaryButton, 18)
        setContent(text, iconRes, iconFirst)
        onTap(18, palette.ripple) { onClick() }
    }

fun BaseActivity.progressBar(fraction: Float, fill: Int = palette.progressFill(), track: Int = palette.track) =
    ProgressBarView(this, fill, track).apply { this.fraction = fraction }

fun BaseActivity.avatar(sizeDp: Int, textSp: Float, ring: Boolean = false): FrameLayout = FrameLayout(this).apply {
    background = if (ring) oval(palette.tint, palette.avatarRing, 3) else oval(palette.tint)
    place(label(settings.initial, textSp, palette.primary, 800) { gravity = Gravity.CENTER }, MATCH, MATCH)
}

fun BaseActivity.chevron(): ImageView = icon(R.drawable.ic_chevron_right, palette.textMuted)

fun BaseActivity.divider(): View = View(this).apply { setBackgroundColor(palette.border) }

fun BaseActivity.settingsSubtitle(text: String): TextView = label(text, 14f, palette.textSecondary)

fun BaseActivity.settingsRow(
    iconRes: Int,
    title: String,
    subtitle: String,
    trailing: View? = null,
    onClick: (() -> Unit)? = null,
): LinearLayout = settingsRow(iconRes, title, settingsSubtitle(subtitle), trailing, onClick)

fun BaseActivity.settingsRow(
    iconRes: Int,
    title: String,
    subtitle: TextView,
    trailing: View? = null,
    onClick: (() -> Unit)? = null,
): LinearLayout = horizontal {
    pad(20, 16)
    place(iconTile(iconRes, palette.primary, palette.tint, iconSizeDp = 22), 44, 44)
    place(vertical {
        place(label(title, 17f, palette.text, 700))
        place(subtitle, top = 3)
    }, 0, WRAP, 1f, start = 16, end = 12)
    trailing?.let { place(it, WRAP, WRAP) }
    onClick?.let { action -> onTap(0, palette.ripple, action) }
}

fun LinearLayout.rowDivider(activity: BaseActivity) {
    place(activity.divider(), MATCH, 1, start = 20, end = 20)
}

fun BaseActivity.backLink(text: String): LinearLayout = horizontal {
    place(icon(R.drawable.ic_chevron_left, palette.primary), 24, 24)
    place(label(text, 18f, palette.primary, 600), WRAP, WRAP, start = 6)
    pad(0, 6, 8, 6)
    onTap(12, palette.ripple) { finish() }
}

fun BaseActivity.infoNote(iconRes: Int, text: String): LinearLayout = horizontal {
    gravity = Gravity.TOP
    place(icon(iconRes, palette.accent), 20, 20, top = 1)
    place(label(text, 14.5f, palette.textSecondary), 0, WRAP, 1f, start = 12)
}

fun BaseActivity.pill(text: String, textColor: Int, background: Int, iconRes: Int? = null, sizeSp: Float = 14f): LinearLayout =
    horizontal {
        this.background = roundedRect(background, 100)
        pad(12, 6)
        iconRes?.let { place(icon(it, textColor), 16, 16, end = 6) }
        place(label(text, sizeSp, textColor, 700), WRAP, WRAP)
    }

fun BaseActivity.settingsHeader(column: LinearLayout, title: String, description: String) {
    column.place(backLink("Profil"), WRAP, WRAP, bottom = 20)
    column.place(eyebrow("Ustawienia"))
    column.place(screenTitle(title), top = 6)
    column.place(bodyText(description), top = 10, bottom = 24)
}

fun LinearLayout.section(activity: BaseActivity, title: String, top: Int = 28) {
    place(activity.sectionTitle(title), top = top, bottom = 14)
}

fun LinearLayout.titleRow(activity: BaseActivity, title: String, trailing: String, top: Int = 28) {
    place(activity.horizontal {
        place(activity.sectionTitle(title), 0, WRAP, 1f)
        place(activity.label(trailing, 16f, activity.palette.accent, 600) { ellipsize = TextUtils.TruncateAt.END }, WRAP, WRAP)
    }, top = top, bottom = 14)
}

fun Palette.progressFill(): Int = if (isDark) primary else button
