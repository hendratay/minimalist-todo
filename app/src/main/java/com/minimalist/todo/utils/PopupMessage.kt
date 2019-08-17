package com.minimalist.todo.utils

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import com.minimalist.todo.R

fun snackBar(view: View, message: CharSequence, action: CharSequence, listener: (View) -> Unit) {
    val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snackBar.view.apply {
        setBackgroundColor(Color.BLACK)
        val text = findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        val actionText = findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        text.apply {
            setTypeface(ResourcesCompat.getFont(context, R.font.montserrat), Typeface.NORMAL)
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }
        actionText.apply {
            setTypeface(ResourcesCompat.getFont(context, R.font.montserrat), Typeface.BOLD)
            setTextColor(Color.parseColor("#6FCF97"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        }
    }
    snackBar.setAction(action, listener)
    snackBar.show()
}