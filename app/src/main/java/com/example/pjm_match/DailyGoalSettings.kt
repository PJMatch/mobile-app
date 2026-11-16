package com.example.pjm_match

import android.content.Context

object DailyGoalSettings {
    val options = listOf(20, 50, 100, 150)

    fun get(context: Context): Int =
        context.getSharedPreferences("daily_goal", Context.MODE_PRIVATE)
            .getInt("xp", 50).takeIf { it in options } ?: 50

    fun save(context: Context, xp: Int) {
        require(xp in options)
        context.getSharedPreferences("daily_goal", Context.MODE_PRIVATE)
            .edit().putInt("xp", xp).apply()
    }
}
