package com.example.user.whattodo.activity

import android.content.IntentSender
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.user.whattodo.BuildConfig
import com.example.user.whattodo.R
import com.example.user.whattodo.utils.Gmail
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import kotlinx.android.synthetic.main.activity_send_feedback.*
import kotlin.concurrent.thread

class SendFeedbackActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_EMAIL = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_feedback)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupTextFrom()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> NavUtils.navigateUpFromSameTask(this)
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        NavUtils.navigateUpFromSameTask(this)
    }

    private fun setupTextFrom() {
        try {
            val intent = AccountPicker.newChooseAccountIntent(
                    null, null, arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE), true,
                    null, null, null, null)
            intent.putExtra("overrideTheme", 1)
            startActivityForResult(intent, REQUEST_CODE_EMAIL)
        } catch (e: IntentSender.SendIntentException) {
        }
    }

    private fun sendFeedback() {
        val progressDialog = progressDialog()
        thread {
            runOnUiThread { progressDialog.show() }
            try {
                val sender = Gmail(BuildConfig.APP_GMAIL, BuildConfig.APP_GMAIL_PASS)
                sender.sendMail("SUBJECT",
                        "FEEDBACK",
                        "FROM",
                        BuildConfig.CUSTOMER_SERVICE_GMAIL)
            } catch (e: Exception) {
            }
            runOnUiThread { progressDialog.dismiss() }
        }
    }

    private fun progressDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.dialog_progress, null))
        return builder.create()
    }

}