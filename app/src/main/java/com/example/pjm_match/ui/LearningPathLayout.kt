package com.example.pjm_match.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.view.ViewGroup

class LearningPathLayout(context: Context) : ViewGroup(context) {

    enum class Side { LEFT, RIGHT }

    data class Slot(val xFraction: Float, val yDp: Int, val sizeDp: Int, val side: Side, val labelFraction: Float)

    private class Entry(val slot: Slot, val node: View, val label: View, val reached: Boolean)

    private val entries = mutableListOf<Entry>()
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = context.dpf(6)
    }
    private val path = Path()

    init {
        setWillNotDraw(false)
        clipChildren = false
    }

    fun addStep(slot: Slot, node: View, label: View, reached: Boolean) {
        entries += Entry(slot, node, label, reached)
        addView(node)
        addView(label)
    }

    private fun centerX(entry: Entry) = entry.slot.xFraction * width
    private fun centerY(entry: Entry) = context.dpf(entry.slot.yDp)

    private fun labelBounds(entry: Entry, totalWidth: Int): Pair<Int, Int> {
        val nodeX = (entry.slot.xFraction * totalWidth).toInt()
        val radius = context.dp(entry.slot.sizeDp) / 2
        val start = (entry.slot.labelFraction * totalWidth).toInt()
        return when (entry.slot.side) {
            Side.RIGHT -> start to totalWidth
            Side.LEFT -> start to (nodeX - radius - context.dp(12))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        for (entry in entries) {
            val size = context.dp(entry.slot.sizeDp)
            entry.node.measure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY))
            val (start, end) = labelBounds(entry, width)
            entry.label.measure(
                MeasureSpec.makeMeasureSpec((end - start).coerceAtLeast(0), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            )
            val bottom = context.dp(entry.slot.yDp) + maxOf(size, entry.label.measuredHeight) / 2
            height = maxOf(height, bottom)
        }
        setMeasuredDimension(width, height + context.dp(8))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        for (entry in entries) {
            val cx = (entry.slot.xFraction * width).toInt()
            val cy = context.dp(entry.slot.yDp)
            val half = entry.node.measuredWidth / 2
            entry.node.layout(cx - half, cy - half, cx + half, cy + half)
            val (start, _) = labelBounds(entry, width)
            val top = cy - entry.label.measuredHeight / 2
            entry.label.layout(start, top, start + entry.label.measuredWidth, top + entry.label.measuredHeight)
        }
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0 until entries.size - 1) {
            val a = entries[i]
            val b = entries[i + 1]
            val ax = centerX(a)
            val ay = centerY(a)
            val bx = centerX(b)
            val by = centerY(b)
            val dy = by - ay
            path.reset()
            path.moveTo(ax, ay)
            path.cubicTo(ax + (bx - ax) * 0.15f, ay + dy * 0.75f, bx - (bx - ax) * 0.15f, by - dy * 0.75f, bx, by)
            linePaint.color = if (b.reached) Palette.primary else Palette.lockedPath
            canvas.drawPath(path, linePaint)
        }
    }
}
