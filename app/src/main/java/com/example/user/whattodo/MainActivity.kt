package com.example.user.whattodo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.user.whattodo.adapter.PagerAdapter
import com.example.user.whattodo.db.TodoDatabase
import kotlinx.android.synthetic.main.activity_main.*;
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var database : TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
    }

    private fun setupNavigation() {
        view_pager.adapter = PagerAdapter(supportFragmentManager)
        tab_layout.setupWithViewPager(view_pager)
        if(intent.getStringExtra("fragment") == "reminderFragment" ) {
            view_pager.currentItem = 1
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.getStringExtra("fragment") == "reminderFragment" ) {
            view_pager.currentItem = 1
        }
    }

}
