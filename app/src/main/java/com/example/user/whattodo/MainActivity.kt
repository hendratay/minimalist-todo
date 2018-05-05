package com.example.user.whattodo

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.db.TodoEntity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_done.*
import kotlinx.android.synthetic.main.activity_main.*;
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var database : TodoDatabase
    private lateinit var adapter: TodoAdapter

    private var TodoList: MutableList<Todo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(tb_main)
        tb_main.setBackgroundColor(Color.WHITE)

        App.component.inject(this)

        fab_add_todo.setOnClickListener{
            addTodoDialog()
        }

        rv_todo_list.layoutManager = LinearLayoutManager(this)
        adapter = TodoAdapter(TodoList, { todo: Todo -> onItemChecked(todo)}, { todoList: List<Todo> -> deleteTodo(todoList)})
        rv_todo_list.adapter = adapter

        refreshTodoRecycler()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.action_done -> {
            doneActivity()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    fun addTodoDialog() {
        val alert = AlertDialog.Builder(this)
        val todoEditText = EditText(this)
        todoEditText.hint = "Enter Todo"

        alert.setTitle("Add New Todo")
        // TODO: Use custom layout
        alert.setView(todoEditText, 48, 48, 48, 0)

        alert.setPositiveButton("Add") {
            dialog, _ ->

            database.todoDao().insertTodo(TodoEntity(todoEditText.text.toString(), false))
            refreshTodoRecycler()

            dialog.dismiss()
        }
        alert.setNegativeButton("Cancel") {
            dialog, _ ->

            dialog.dismiss()
        }

        alert.show()
    }

    fun refreshTodoRecycler() {
        database.todoDao().getTodo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    TodoList.clear()
                    for (todo: TodoEntity in it) {
                        TodoList.add(Todo(todo.id, todo.todo, todo.done))
                    }
                    // For fixing java.lang.IndexOutOfBoundsException: Inconsistency detected
                    rv_todo_list.recycledViewPool.clear()
                    adapter.notifyDataSetChanged()
                }
    }

    fun onItemChecked(todo: Todo) {
        TodoList.remove(todo)
        if(!rv_todo_list.isComputingLayout) {
            adapter.notifyDataSetChanged()
            moveTodoToDone(todo)
            Snackbar.make(cl_main, todo.todoText + " have done", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun moveTodoToDone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, true)
        Single.fromCallable { database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun deleteTodo(todoList: List<Todo>) {
        val backup: MutableList<TodoEntity> = ArrayList()
        todoList.forEach {
            val entity = TodoEntity(it.todoId, it.todoText, it.done)
            backup.add(entity)
            Single.fromCallable { database.todoDao().deleteTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        val snackbar = Snackbar.make(cl_main, todoList.size.toString() + " item deleted", Snackbar.LENGTH_SHORT)
        snackbar.show()
        snackbar.setAction("UNDO", View.OnClickListener {
            backup.forEach {
                database.todoDao().insertTodo(it)
                refreshTodoRecycler()
            }
        } )
    }

    fun doneActivity() {
        val intent = Intent(this, DoneActivity::class.java)
        startActivity(intent)
    }
}
