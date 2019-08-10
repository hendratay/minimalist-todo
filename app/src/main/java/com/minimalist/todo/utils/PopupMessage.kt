package com.minimalist.todo.utils

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

fun snackBar(view: View, message: CharSequence, action: CharSequence, listener: (View) -> Unit) {
    val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackBar.view.apply {
        setBackgroundColor(Color.WHITE)
        val text = findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        text.apply {
            setTextColor(Color.DKGRAY)
        }
    }
    snackBar.setAction(action, listener)
    snackBar.setActionTextColor(Color.BLACK)
    snackBar.show()
}