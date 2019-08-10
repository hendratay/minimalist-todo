package com.minimalist.todo.activity

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.minimalist.todo.BuildConfig
import com.minimalist.todo.R
import com.minimalist.todo.utils.Gmail
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE_EMAIL) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val email = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                }
                Activity.RESULT_CANCELED -> finish()
            }
        }
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