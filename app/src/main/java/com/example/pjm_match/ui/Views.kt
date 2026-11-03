package com.example.pjm_match.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import kotlin.math.roundToInt

const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

fun Context.dp(value: Number): Int = (value.toFloat() * resources.displayMetrics.density).roundToInt()

fun Context.dpf(value: Number): Float = value.toFloat() * resources.displayMetrics.density

object Fonts {
    private val cache = HashMap<Int, Typeface>()

    fun weight(weight: Int): Typeface = cache.getOrPut(weight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface.create(Typeface.SANS_SERIF, weight, false)
        } else when {
            weight >= 700 -> Typeface.create("sans-serif", Typeface.BOLD)
            weight >= 500 -> Typeface.create("sans-serif-medium", Typeface.NORMAL)
            else -> Typeface.SANS_SERIF
        }
    }
}

fun Context.roundedRect(color: Int, radiusDp: Number, strokeColor: Int = Color.TRANSPARENT, strokeDp: Number = 0) =
    GradientDrawable().apply {
        setColor(color)
        cornerRadius = dpf(radiusDp)
        if (strokeDp.toFloat() > 0f) setStroke(dp(strokeDp).coerceAtLeast(1), strokeColor)
    }

fun Context.oval(color: Int, strokeColor: Int = Color.TRANSPARENT, strokeDp: Number = 0) =
    GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
        if (strokeDp.toFloat() > 0f) setStroke(dp(strokeDp).coerceAtLeast(1), strokeColor)
    }

fun Context.label(
    text: CharSequence,
    sizeSp: Float,
    color: Int,
    weight: Int = 400,
    block: TextView.() -> Unit = {},
): TextView = TextView(this).apply {
    this.text = text
    textSize = sizeSp
    setTextColor(color)
    typeface = Fonts.weight(weight)
    setLineSpacing(0f, 1.12f)
    block()
}

class IconSpec(@DrawableRes val res: Int, val widthDp: Float, val heightDp: Float)

class IconView(context: Context) : ImageView(context) {
    var spec: IconSpec? = null
        set(value) {
            field = value
            value?.let { setImageResource(it.res) }
            requestLayout()
        }

    init {
        scaleType = ScaleType.FIT_CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val current = spec ?: return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(context.dp(current.widthDp), context.dp(current.heightDp))
    }
}

fun Context.icon(spec: IconSpec, color: Int): IconView = IconView(this).apply {
    this.spec = spec
    imageTintList = ColorStateList.valueOf(color)
}

fun Context.iconTile(
    spec: IconSpec,
    iconColor: Int,
    background: Int,
    sizeDp: Int = 44,
    radiusDp: Number = 14,
): FrameLayout = FrameLayout(this).apply {
    this.background = roundedRect(background, radiusDp)
    place(icon(spec, iconColor), WRAP, WRAP, gravity = Gravity.CENTER)
    minimumWidth = dp(sizeDp)
    minimumHeight = dp(sizeDp)
}

fun Context.vertical(block: LinearLayout.() -> Unit = {}) = LinearLayout(this).apply {
    orientation = LinearLayout.VERTICAL
    block()
}

fun Context.horizontal(block: LinearLayout.() -> Unit = {}) = LinearLayout(this).apply {
    orientation = LinearLayout.HORIZONTAL
    gravity = Gravity.CENTER_VERTICAL
    block()
}

fun <T : View> ViewGroup.place(
    view: T,
    width: Int = MATCH,
    height: Int = WRAP,
    weight: Float = 0f,
    top: Int = 0,
    bottom: Int = 0,
    start: Int = 0,
    end: Int = 0,
    gravity: Int = Gravity.NO_GRAVITY,
): T {
    val w = if (width > 0) context.dp(width) else width
    val h = if (height > 0) context.dp(height) else height
    val params: ViewGroup.MarginLayoutParams = when (this) {
        is LinearLayout -> LinearLayout.LayoutParams(w, h, weight).also { if (gravity != Gravity.NO_GRAVITY) it.gravity = gravity }
        is FrameLayout -> FrameLayout.LayoutParams(w, h).also { if (gravity != Gravity.NO_GRAVITY) it.gravity = gravity }
        else -> ViewGroup.MarginLayoutParams(w, h)
    }
    params.setMargins(context.dp(start), context.dp(top), context.dp(end), context.dp(bottom))
    addView(view, params)
    return view
}

fun LinearLayout.spacer(weight: Float = 1f): View = place(View(context), 0, 0, weight)

fun View.pad(horizontal: Number, vertical: Number = horizontal) {
    setPadding(context.dp(horizontal), context.dp(vertical), context.dp(horizontal), context.dp(vertical))
}

fun View.pad(start: Number, top: Number, end: Number, bottom: Number) {
    setPadding(context.dp(start), context.dp(top), context.dp(end), context.dp(bottom))
}

fun View.onTap(radiusDp: Number, rippleColor: Int, action: () -> Unit) {
    foreground = RippleDrawable(ColorStateList.valueOf(rippleColor), null, context.roundedRect(Color.WHITE, radiusDp))
    isClickable = true
    isFocusable = true
    setOnClickListener { action() }
}

fun Int.withAlpha(alpha: Float): Int =
    Color.argb((alpha * 255).roundToInt(), Color.red(this), Color.green(this), Color.blue(this))
