package com.minimalist.todo.activity

import android.annotation.TargetApi
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.minimalist.todo.adapter.PagerAdapter
import com.minimalist.todo.db.TodoDatabase
import com.minimalist.todo.R
import com.minimalist.todo.widget.TodoWidget
import com.minimalist.todo.App
import com.minimalist.todo.fragment.GroceryFragment
import com.minimalist.todo.fragment.ReminderFragment
import com.minimalist.todo.fragment.TaskFragment
import com.minimalist.todo.fragment.TodoFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var database: TodoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupNavigation()
        setupAddTodoButton()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.open_source_license -> startActivity(Intent(this, OpenSourceLicenseActivity::class.java))
            R.id.send_feedback -> openGithubIssues()
            R.id.rate_app -> openGooglePlayStore()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupToolbar() {
        val sdf = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
        toolbar_title.text = sdf.format(Date(Calendar.getInstance().timeInMillis))
        setSupportActionBar(toolbar)
    }

    override fun onPause() {
        super.onPause()
        updateWidget()
    }

    private fun setupNavigation() {
        view_pager.adapter = PagerAdapter(supportFragmentManager)
        view_pager.offscreenPageLimit = 3
        tab_layout.setupWithViewPager(view_pager)
        if (intent.getStringExtra("fragment") == "reminderFragment") {
            view_pager.currentItem = 2
        }
        if (view_pager.currentItem == 0) fab_add_todo.hide()
    }

    private fun setupAddTodoButton() {
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                val fragment = supportFragmentManager
                        .findFragmentByTag("android:switcher:${R.id.view_pager}:${view_pager.currentItem}") as TodoFragment
                fragment.destroyActionCallback()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
                    0 -> fab_add_todo.hide()
                    1 -> fab_add_todo.show()
                    2 -> fab_add_todo.show()
                    3 -> fab_add_todo.show()
                }
            }

            override fun onPageSelected(position: Int) {
                val fragment = supportFragmentManager.findFragmentByTag("android:switcher:${R.id.view_pager}:$position")
                fab_add_todo.setOnClickListener {
                    when (position) {
                        1 -> (fragment as TaskFragment).addTaskDialog()
                        2 -> (fragment as ReminderFragment).addReminderDialog()
                        3 -> (fragment as GroceryFragment).addGroceryDialog()
                    }
                }
            }
        })
    }

    private fun updateWidget() {
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, TodoWidget::class.java))
        AppWidgetManager.getInstance(this).notifyAppWidgetViewDataChanged(ids, R.id.appwidget_list_view)
    }

    private fun openGithubIssues() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/hendratay/minimalist-todo/issues"))
        startActivity(browserIntent)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun openGooglePlayStore() {
        val uri = Uri.parse("market://details?id=${this.packageName}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${this.packageName}")))
        }
    }

}
