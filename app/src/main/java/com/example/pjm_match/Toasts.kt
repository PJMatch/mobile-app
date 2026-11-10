package com.example.pjm_match

import android.content.Context
import android.widget.Toast

private var currentToast: Toast? = null

fun Context.toast(text: CharSequence, long: Boolean = false) {
    currentToast?.cancel()
    currentToast = Toast.makeText(applicationContext, text, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).apply { show() }
}
