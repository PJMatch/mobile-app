package com.example.pjm_match

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val button = Button(this).apply {
            text = "Wybierz znak"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, SignListActivity::class.java))
            }
        }

        setContentView(LinearLayout(this).apply {
            gravity = Gravity.CENTER
            addView(button)
        })
    }
}
