package com.minimalist.todo.activity

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.minimalist.todo.R
import com.minimalist.todo.adapter.TodoAdapter
import com.minimalist.todo.db.TodoDatabase
import com.minimalist.todo.db.TodoEntity
import com.minimalist.todo.model.Todo
import com.minimalist.todo.utils.getOrdinalNumber
import com.minimalist.todo.utils.snackBar
import com.minimalist.todo.widget.TodoWidget
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var database: TodoDatabase? = null
    private val backup: MutableList<TodoEntity> = ArrayList()
    private lateinit var adapter: TodoAdapter
    private var todoList: MutableList<Todo> = ArrayList()
    private var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        database = TodoDatabase.getDatabase(this)
        setupToolbar()
        setupTodoRecyclerView()
        setupAddTodo()
        hideEmptyViewWhenKeyboardShown()
    }

    private fun hideEmptyViewWhenKeyboardShown() {
        main_layout.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            main_layout.getWindowVisibleDisplayFrame(rect)
            val screenHeight = main_layout.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                empty_view.visibility = View.GONE
            } else {
                Handler().postDelayed({
                    empty_view.visibility = if (todoList.isEmpty()) View.VISIBLE else View.GONE
                }, 100)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getTodo()
        edit_text_todo.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        updateWidget()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> toolbar.showOverflowMenu()
            R.id.about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.open_source_license -> startActivity(Intent(this, OpenSourceLicenseActivity::class.java))
            R.id.send_feedback -> openGithubIssues()
            R.id.rate_app -> openGooglePlayStore()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupToolbar() {
        val sdfDay = SimpleDateFormat("EEEE", Locale.US)
        val ordinalNumber = getOrdinalNumber(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        val sdfDate = SimpleDateFormat("dd'$ordinalNumber' MMMM YYYY", Locale.US)
        toolbar_day.text = sdfDay.format(Calendar.getInstance().time)
        toolbar_date.text = sdfDate.format(Calendar.getInstance().time)
        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp)
        setSupportActionBar(toolbar)
    }

    private fun openGithubIssues() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/hendratay/minimalist-todo/issues"))
        startActivity(browserIntent)
    }

    private fun openGooglePlayStore() {
        startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${this.packageName}")))
    }

    private fun setupTodoRecyclerView() {
        adapter = TodoAdapter(todoList, { todo: Todo -> onItemChecked(todo) }, { todoList: Todo -> onItemDeleted(todoList) })
        recycler_view_todo.layoutManager = LinearLayoutManager(this)
        recycler_view_todo.adapter = adapter
    }

    private fun setupAddTodo() {
        edit_text_todo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) addTodo()
            false
        }
        button_add_todo.setOnClickListener { view ->
            addTodo()
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun onItemChecked(todo: Todo) {
        if (!recycler_view_todo.isComputingLayout) {
            if (todo.done) updateTodo(todo, false) else updateTodo(todo, true)
        }
    }

    private fun onItemDeleted(item: Todo) {
        deleteTodo(item)
        snackBar(main_layout, "\"${item.todoText}\" has been deleted.", resources.getString(R.string.undo)) {
            undoDeleteTodo()
        }
    }

    private fun getTodo() {
        if (database != null) {
            compositeDisposable.add(database!!.todoDao().getTodo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { todo ->
                        todoList.clear()
                        todo.forEach { todoList.add(Todo(it.id, it.todo, it.done)) }
                        empty_view.visibility = if (todoList.isEmpty()) View.VISIBLE else View.GONE
                        adapter.notifyDataSetChanged()
                    })
        }
    }

    private fun addTodo() {
        if (edit_text_todo.text.isNotBlank() && database != null) {
            compositeDisposable.add(Single.fromCallable { database!!.todoDao().insertTodo(TodoEntity(edit_text_todo.text.toString(), false)) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { _ ->
                        edit_text_todo.text.clear()
                    })
        }
    }

    private fun updateTodo(todo: Todo, done: Boolean) {
        val entity = TodoEntity(todo.todoId, todo.todoText, done)
        if (database != null) {
            compositeDisposable.add(Single.fromCallable { database!!.todoDao().updateTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe())
        }
    }

    private fun deleteTodo(item: Todo) {
        backup.clear()
        val entity = TodoEntity(item.todoId, item.todoText, item.done)
        backup.add(entity)
        if (database != null) {
            compositeDisposable.add(Single.fromCallable { database!!.todoDao().deleteTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe())
        }
    }

    private fun undoDeleteTodo() {
        if (database != null) {
            backup.forEach {
                compositeDisposable.add(Single.fromCallable { database!!.todoDao().insertTodo(it) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe())
            }
        }
    }

    private fun updateWidget() {
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, TodoWidget::class.java))
        AppWidgetManager.getInstance(this).notifyAppWidgetViewDataChanged(ids, R.id.appwidget_list_view)
    }

}
