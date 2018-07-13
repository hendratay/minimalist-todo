package com.example.user.whattodo

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import com.example.user.whattodo.adapter.PagerAdapter
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.fragment.GroceryFragment
import com.example.user.whattodo.fragment.ReminderFragment
import com.example.user.whattodo.fragment.TaskFragment
import com.example.user.whattodo.fragment.TodoFragment
import com.example.user.whattodo.widget.TodoWidget
import kotlinx.android.synthetic.main.activity_main.*;
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var database : TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupNavigation()
        setupAddTodoButton()
    }

    private fun setupToolbar() {
        val sdf = SimpleDateFormat("EEEE, MMMM dd")
        toolbar_title.text = sdf.format(Date(Calendar.getInstance().timeInMillis))
        setSupportActionBar(toolbar)
    }

    override fun onPause() {
        onBackPressed()
        super.onPause()
        updateWidget()
    }

    private fun setupNavigation() {
        view_pager.adapter = PagerAdapter(supportFragmentManager)
        tab_layout.setupWithViewPager(view_pager)
        if(intent.getStringExtra("fragment") == "reminderFragment" ) {
            view_pager.currentItem = 2
        }
    }

    private fun setupAddTodoButton() {
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                val fragment = supportFragmentManager
                        .findFragmentByTag("android:switcher:${R.id.view_pager}:${view_pager.currentItem}") as TodoFragment
                fragment.destroyActionCallback()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                fab_add_todo.setOnClickListener {
                    when(position) {
                        1 -> {
                            val taskFragment = supportFragmentManager
                                    .findFragmentByTag("android:switcher:${R.id.view_pager}:${view_pager.currentItem}")
                                    as TaskFragment
                            taskFragment.addTaskDialog()
                        }
                        2 -> {
                            val reminderFragment = supportFragmentManager
                                    .findFragmentByTag("android:switcher:${R.id.view_pager}:${view_pager.currentItem}")
                                    as ReminderFragment
                            reminderFragment.addReminderDialog()
                        }
                        3 -> {
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

    private fun updateWidget() {
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, TodoWidget::class.java))
        AppWidgetManager.getInstance(this).notifyAppWidgetViewDataChanged(ids, R.id.appwidget_list_view);
    }

}
