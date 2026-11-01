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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.ThemeMode
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

enum class Tab(val label: String, val iconRes: Int, val activityClass: Class<out Activity>) {
    START("Start", R.drawable.ic_home, MainActivity::class.java),
    LEARN("Nauka", R.drawable.ic_route, LearningPathActivity::class.java),
    TRANSLATOR("Tłumacz AI", R.drawable.ic_scan, AiTranslatorActivity::class.java),
    STATS("Statystyki", R.drawable.ic_chart, StatsActivity::class.java),
    PROFILE("Profil", R.drawable.ic_user, ProfileActivity::class.java),
}

abstract class BaseActivity : AppCompatActivity() {

    lateinit var palette: Palette
        private set
    lateinit var settings: AppSettings
        private set

    private var appliedThemeMode: ThemeMode? = null

    protected open val tabRoot: Tab? = null

    protected open fun screenBackground(): Int = palette.background

    override fun onCreate(savedInstanceState: Bundle?) {
        settings = AppSettings(this)
        appliedThemeMode = settings.themeMode
        delegate.localNightMode = when (settings.themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        super.onCreate(savedInstanceState)
        palette = Palette.resolve(this, settings.themeMode)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !palette.isDark
            isAppearanceLightNavigationBars = !palette.isDark
        }
        window.decorView.setBackgroundColor(screenBackground())
    }

    override fun onResume() {
        super.onResume()
        if (settings.themeMode != appliedThemeMode) recreate()
    }

    fun scaffold(
        navTab: Tab?,
        footer: (LinearLayout.() -> Unit)? = null,
        content: LinearLayout.() -> Unit,
    ): LinearLayout {
        val root = vertical { setBackgroundColor(screenBackground()) }
        val scroll = ScrollView(this).apply {
            isFillViewport = true
            isVerticalScrollBarEnabled = false
        }
        val column = vertical()
        scroll.addView(column, FrameLayout.LayoutParams(MATCH, WRAP))
        root.place(scroll, MATCH, 0, 1f)

        val footerView = footer?.let { build ->
            root.place(vertical {
                setBackgroundColor(screenBackground())
                build()
            })
        }
        val nav = navTab?.let { root.place(bottomNav(it)) }

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val bottomInset = if (imeVisible) ime.bottom else bars.bottom
            column.setPadding(dp(24) + bars.left, bars.top + dp(28), dp(24) + bars.right, dp(28))
            footerView?.setPadding(dp(24) + bars.left, dp(8), dp(24) + bars.right, dp(16))
            nav?.visibility = if (imeVisible) View.GONE else View.VISIBLE
            val navShown = nav != null && !imeVisible
            nav?.setPadding(bars.left, 0, bars.right, bars.bottom)
            root.setPadding(0, 0, 0, if (navShown) 0 else bottomInset)
            insets
        }
        column.content()
        setContentView(root)
        return column
    }

    private fun bottomNav(active: Tab): LinearLayout = vertical {
        setBackgroundColor(palette.surface)
        place(View(context).apply { setBackgroundColor(palette.border) }, MATCH, 1)
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
            if (active) background = roundedRect(palette.tint, 14)
        }, 60, 36)
        pill.place(icon(tab.iconRes, if (active) palette.primary else palette.textMuted), 22, 22, gravity = Gravity.CENTER)
        place(
            label(tab.label, 11.5f, if (active) palette.primary else palette.textSecondary, if (active) 700 else 500) {
                maxLines = 1
            },
            WRAP, WRAP, top = 6,
        )
        onTap(16, palette.ripple) { openTab(tab) }
    }

    fun openTab(tab: Tab) {
        if (tab == tabRoot) return
        val home = Intent(this, MainActivity::class.java).addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
        )
        if (tab == Tab.START) {
            startActivity(home)
        } else {
            startActivities(arrayOf(home, Intent(this, tab.activityClass).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)))
        }
    }
}
