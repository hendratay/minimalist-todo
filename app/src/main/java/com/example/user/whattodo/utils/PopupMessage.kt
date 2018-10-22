package com.example.user.whattodo.utils

import android.graphics.Color
import android.graphics.Typeface
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView

fun snackBar(view: View, message: CharSequence, action: CharSequence, listener: (View) -> Unit) {
    val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackBar.view.apply {
        setBackgroundColor(Color.WHITE)
        val text = findViewById<TextView>(android.support.design.R.id.snackbar_text)
        text.apply {
            setTextColor(Color.DKGRAY)
        }
    }
    snackBar.setAction(action, listener)
    snackBar.setActionTextColor(Color.BLACK)
    snackBar.show()
}