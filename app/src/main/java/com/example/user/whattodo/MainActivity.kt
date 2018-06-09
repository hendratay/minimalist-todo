package com.example.user.whattodo

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
        setupTab()
    }

    private fun setupTab() {
        tab_layout.apply {
            setSelectedTabIndicatorColor(Color.TRANSPARENT)
            getTabAt(0)?.setIcon(R.drawable.selector_task)
            getTabAt(1)?.setIcon(R.drawable.selector_reminder)
            getTabAt(2)?.setIcon(R.drawable.selector_grocery)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.action_done -> {
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
