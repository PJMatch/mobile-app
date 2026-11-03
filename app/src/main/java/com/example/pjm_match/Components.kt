package com.example.pjm_match

import android.content.Context
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pjm_match.ui.IconSpec
import com.example.pjm_match.ui.IconView
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.ProgressBarView
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.oval
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical

class ActionButton(context: Context, private val textColor: Int) : LinearLayout(context) {
    private val iconStart: IconView = IconView(context).apply { imageTintList = ColorStateList.valueOf(textColor) }
    private val iconEnd: IconView = IconView(context).apply { imageTintList = ColorStateList.valueOf(textColor) }
    private val text: TextView = context.label("", 17f, textColor, 700) { maxLines = 1 }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        pad(20, 0)
        place(iconStart, WRAP, WRAP, end = 12)
        place(text, WRAP, WRAP)
        place(iconEnd, WRAP, WRAP, start = 12)
    }

    fun setContent(label: String, icon: IconSpec? = null, iconFirst: Boolean = false) {
        text.text = label
        iconStart.visibility = View.GONE
        iconEnd.visibility = View.GONE
        if (icon != null) {
            val target = if (iconFirst) iconStart else iconEnd
            target.spec = icon
            target.visibility = View.VISIBLE
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alpha = if (enabled) 1f else 0.55f
    }
}

fun Context.eyebrow(text: String, color: Int = Palette.accent): TextView =
    label(text.uppercase(), 12.5f, color, 700) { letterSpacing = 0.04f }

fun Context.screenTitle(text: String, color: Int = Palette.text): TextView =
    label(text, 34f, color, 800) { letterSpacing = -0.01f }

fun Context.bodyText(text: String): TextView = label(text, 16f, Palette.textSecondary, 400)

fun Context.sectionTitle(text: String): TextView = label(text, 22f, Palette.text, 700)

fun Context.card(
    padding: Int = 20,
    background: Int = Palette.surface,
    border: Int = Palette.border,
    block: LinearLayout.() -> Unit = {},
): LinearLayout = vertical {
    this.background = roundedRect(background, 24, border, 1)
    pad(padding)
    block()
}

fun Context.primaryButton(text: String, icon: IconSpec? = null, iconFirst: Boolean = false, onClick: () -> Unit): ActionButton =
    ActionButton(this, Palette.onPrimary).apply {
        background = roundedRect(Palette.primary, 18)
        setContent(text, icon, iconFirst)
        onTap(18, 0x33FFFFFF) { if (isEnabled) onClick() }
    }

fun Context.progressBar(fraction: Float, fill: Int = Palette.primary, track: Int = Palette.tint) =
    ProgressBarView(this, fill, track).apply { this.fraction = fraction }

fun Context.avatar(initial: String, sizeSp: Float): FrameLayout = FrameLayout(this).apply {
    background = oval(Palette.tint, Palette.surface, 3)
    place(label(initial, sizeSp, Palette.primary, 800) { gravity = Gravity.CENTER }, MATCH, MATCH)
}

fun Context.pill(text: String, textColor: Int, background: Int, icon: IconSpec? = null, sizeSp: Float = 14f): LinearLayout =
    horizontal {
        this.background = roundedRect(background, 100)
        pad(12, 6)
        icon?.let { place(icon(it, textColor), WRAP, WRAP, end = 6) }
        place(label(text, sizeSp, textColor, 700), WRAP, WRAP)
    }

object Icons {
    val lessonArrowRight = IconSpec(R.drawable.ic_arrow_right, 13.17f, 13.17f)
    val lessonClose = IconSpec(R.drawable.ic_close, 13.0f, 13.0f)
    val lessonFeedbackCheck = IconSpec(R.drawable.ic_circle_check, 20.67f, 20.67f)
    val lessonOptionCheck = IconSpec(R.drawable.ic_circle_check, 18.17f, 18.17f)
    val lessonPlay = IconSpec(R.drawable.ic_play, 12.84f, 14.25f)
    val lessonReplay = IconSpec(R.drawable.ic_replay, 14.25f, 14.25f)
    val lessonVideo = IconSpec(R.drawable.ic_video, 12.33f, 8.0f)
    val lessonZap = IconSpec(R.drawable.ic_zap, 12.0f, 13.17f)
    val navChart = IconSpec(R.drawable.ic_chart, 19.0f, 17.25f)
    val navHome = IconSpec(R.drawable.ic_home, 17.25f, 18.12f)
    val navRoute = IconSpec(R.drawable.ic_route, 17.25f, 19.0f)
    val navScan = IconSpec(R.drawable.ic_scan, 17.25f, 17.25f)
    val navUser = IconSpec(R.drawable.ic_user, 15.5f, 17.25f)
    val pathArrowRight = IconSpec(R.drawable.ic_arrow_right, 11.42f, 11.42f)
    val pathCheck = IconSpec(R.drawable.ic_check, 15.5f, 11.12f)
    val pathFlame = IconSpec(R.drawable.ic_flame, 9.67f, 12.58f)
    val pathHand = IconSpec(R.drawable.ic_hand, 26.63f, 26.5f)
    val pathHash = IconSpec(R.drawable.ic_hash, 15.5f, 17.25f)
    val pathLock = IconSpec(R.drawable.ic_lock, 17.25f, 19.0f)
    val pathLockSmall = IconSpec(R.drawable.ic_lock, 14.25f, 15.67f)
    val pathMessages = IconSpec(R.drawable.ic_messages, 19.0f, 19.0f)
    val pathPlay = IconSpec(R.drawable.ic_play, 20.17f, 22.5f)
    val pathUsers = IconSpec(R.drawable.ic_users, 19.0f, 17.25f)
    val pathUtensils = IconSpec(R.drawable.ic_utensils, 17.25f, 19.0f)
    val startArrowRight = IconSpec(R.drawable.ic_arrow_right, 12.0f, 12.0f)
    val startArrowUpRight = IconSpec(R.drawable.ic_arrow_up_right, 9.42f, 9.41f)
    val startChart = IconSpec(R.drawable.ic_chart, 20.67f, 18.75f)
    val startFlame = IconSpec(R.drawable.ic_flame, 16.08f, 21.29f)
    val startHand = IconSpec(R.drawable.ic_hand, 34.57f, 33.46f)
    val startScan = IconSpec(R.drawable.ic_scan, 18.75f, 18.75f)
    val startTarget = IconSpec(R.drawable.ic_target, 17.33f, 17.33f)
    val startZap = IconSpec(R.drawable.ic_zap, 19.51f, 21.51f)
    val trCamera = IconSpec(R.drawable.ic_camera, 12.33f, 10.17f)
    val trInfo = IconSpec(R.drawable.ic_info, 15.67f, 15.67f)
    val trScan = IconSpec(R.drawable.ic_scan, 16.5f, 16.5f)
    val trScanFace = IconSpec(R.drawable.ic_scan_face, 12.75f, 12.75f)
    val trSparkles = IconSpec(R.drawable.ic_sparkles, 14.84f, 14.84f)
    val trSwitchCamera = IconSpec(R.drawable.ic_switch_camera, 17.33f, 17.33f)
    val lessonPlaceholderHand = IconSpec(R.drawable.ic_hand, 56f, 54.2f)
    val permissionCamera = IconSpec(R.drawable.ic_camera, 40f, 33f)
    val profileBook = IconSpec(R.drawable.ic_book, 14.17f, 12.75f)
    val profileTarget = IconSpec(R.drawable.ic_target, 15.83f, 15.83f)
    val profileTheme = IconSpec(R.drawable.ic_theme, 15.92f, 15.83f)
    val profileBell = IconSpec(R.drawable.ic_bell, 14.25f, 15.83f)
    val profileUser = IconSpec(R.drawable.ic_user, 12.67f, 14.25f)
    val profileShield = IconSpec(R.drawable.ic_shield, 12.67f, 15.85f)
    val profileLogout = IconSpec(R.drawable.ic_logout, 14.24f, 14.24f)
    val profileChevronRight = IconSpec(R.drawable.ic_chevron_right, 4.25f, 8.5f)
}
