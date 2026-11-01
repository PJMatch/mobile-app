package com.example.pjm_match

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pjm_match.ui.IconSpec
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.dp
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical

enum class Tab(val label: String, val icon: IconSpec, val activityClass: Class<out Activity>?) {
    START("Start", Icons.navHome, MainActivity::class.java),
    LEARN("Nauka", Icons.navRoute, LearningPathActivity::class.java),
    TRANSLATOR("Tłumacz AI", Icons.navScan, AiTranslatorActivity::class.java),
    STATS("Statystyki", Icons.navChart, StatsActivity::class.java),
    PROFILE("Profil", Icons.navUser, null),
}

abstract class BaseActivity : AppCompatActivity() {

    protected open val tabRoot: Tab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        window.decorView.setBackgroundColor(Palette.background)
    }

    fun scaffold(
        navTab: Tab?,
        footer: (LinearLayout.() -> Unit)? = null,
        content: LinearLayout.() -> Unit,
    ): LinearLayout {
        val root = vertical { setBackgroundColor(Palette.background) }
        val scroll = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = false
        }
        val column = vertical()
        scroll.addView(column, FrameLayout.LayoutParams(MATCH, WRAP))
        root.place(scroll, MATCH, 0, 1f)

        val footerView = footer?.let { build ->
            root.place(vertical {
                setBackgroundColor(Palette.background)
                build()
            })
        }
        val nav = navTab?.let { root.place(bottomNav(it)) }

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            column.setPadding(dp(24) + bars.left, bars.top + dp(28), dp(24) + bars.right, dp(28))
            footerView?.setPadding(dp(24) + bars.left, dp(8), dp(24) + bars.right, dp(16))
            nav?.setPadding(bars.left, 0, bars.right, bars.bottom)
            root.setPadding(0, 0, 0, if (nav == null) bars.bottom else 0)
            insets
        }
        column.content()
        setContentView(root)
        return column
    }

    private fun bottomNav(active: Tab): LinearLayout = vertical {
        setBackgroundColor(Palette.surface)
        place(View(context).apply { setBackgroundColor(Palette.border) }, MATCH, 1)
        place(horizontal {
            pad(6, 10, 6, 10)
            gravity = Gravity.TOP
            Tab.entries.forEach { tab -> place(navItem(tab, tab == active), 0, WRAP, 1f) }
        })
    }

    private fun navItem(tab: Tab, active: Boolean): LinearLayout = vertical {
        gravity = Gravity.CENTER_HORIZONTAL
        pad(0, 2)
        contentDescription = tab.label
        val pill = place(FrameLayout(context).apply {
            if (active) background = roundedRect(Palette.tint, 14)
        }, 60, 36)
        pill.place(icon(tab.icon, if (active) Palette.primary else Palette.textMuted), WRAP, WRAP, gravity = Gravity.CENTER)
        place(
            label(tab.label, 11.5f, if (active) Palette.primary else Palette.textSecondary, if (active) 700 else 500) {
                maxLines = 1
            },
            WRAP, WRAP, top = 6,
        )
        onTap(16, Palette.ripple) { openTab(tab) }
    }

    fun openTab(tab: Tab) {
        if (tab == tabRoot) return
        val target = tab.activityClass
        if (target == null) {
            Toast.makeText(this, "${tab.label} w przygotowaniu", Toast.LENGTH_SHORT).show()
            return
        }
        val home = Intent(this, MainActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
        )
        if (tab == Tab.START) {
            startActivity(home)
        } else {
            startActivities(arrayOf(home, Intent(this, target).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)))
        }
    }
}
