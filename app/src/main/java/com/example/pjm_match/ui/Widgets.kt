package com.example.pjm_match.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import kotlin.math.max

class ProgressBarView(context: Context, private val fillColor: Int, private val trackColor: Int) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    var fraction: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        val radius = height / 2f
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        paint.color = trackColor
        canvas.drawRoundRect(rect, radius, radius, paint)
        if (fraction <= 0f) return
        rect.right = max(width * fraction, height.toFloat())
        paint.color = fillColor
        canvas.drawRoundRect(rect, radius, radius, paint)
    }
}
