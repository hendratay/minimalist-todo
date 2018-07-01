package com.example.user.whattodo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.example.user.whattodo.adapter.PagerAdapter
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.fragment.GroceryFragment
import com.example.user.whattodo.fragment.ReminderFragment
import com.example.user.whattodo.fragment.TaskFragment
import kotlinx.android.synthetic.main.activity_main.*;
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var database : TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
        setupAddTodoButton()
    }

    private fun setupNavigation() {
        view_pager.adapter = PagerAdapter(supportFragmentManager)
        tab_layout.setupWithViewPager(view_pager)
        if(intent.getStringExtra("fragment") == "reminderFragment" ) {
            view_pager.currentItem = 1
        }
    }

    private fun setupAddTodoButton() {
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                fab_add_todo.setOnClickListener {
                    when(position) {
                        0 -> {
                            val taskFragment = supportFragmentManager
                                    .findFragmentByTag("android:switcher:${R.id.view_pager}:${view_pager.currentItem}")
                                    as TaskFragment
                            taskFragment.addTaskDialog()
                        }
                        1 -> {
                            val reminderFragment = supportFragmentManager
                                    .findFragmentByTag("android:switcher:${R.id.view_pager}:${view_pager.currentItem}")
                                    as ReminderFragment
                            reminderFragment.addReminderDialog()
                        }
                        2 -> {
                            val groceryFragment = supportFragmentManager
                                    .findFragmentByTag("android:switcher:${R.id.view_pager}:${view_pager.currentItem}")
                                    as GroceryFragment
                            groceryFragment.addGroceryDialog()
                        }
                    }
                }
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

}
