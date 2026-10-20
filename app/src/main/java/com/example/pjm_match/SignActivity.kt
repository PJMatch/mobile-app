package com.example.pjm_match

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView

class SignActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        val sign = intent.getStringExtra(EXTRA_SIGN)

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(TextView(this@SignActivity).apply { text = sign })
            addView(TextView(this@SignActivity).apply { text = "kamera|tutorial" })
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_SIGN = "sign"
    }
}
