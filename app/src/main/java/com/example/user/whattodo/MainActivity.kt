package com.example.user.whattodo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.db.TodoEntity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*;
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var database : TodoDatabase

    private var TodoList: MutableList<Todo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(tb_main)

        App.component.inject(this)

        fab_add_todo.setOnClickListener{
            addTodoDialog()
        }

        rv_todo_list.layoutManager = LinearLayoutManager(this)

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

        alert.setTitle("Enter TODO")
        alert.setMessage("Add a new Todo")
        alert.setView(todoEditText)

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
                    TodoList = convertEntityToTodo(it)
                    rv_todo_list.adapter = TodoAdapter(TodoList, ::onItemChecked, ::deleteTodo)
                }
    }

    fun onItemChecked(todo: Todo) {
        TodoList.remove(todo)
        rv_todo_list.adapter = TodoAdapter(TodoList, ::onItemChecked, ::deleteTodo)
        moveTodoToDone(todo)
        Snackbar.make(cl_main, todo.todoText + " have done", Snackbar.LENGTH_SHORT).show()
    }

    fun moveTodoToDone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, true)
        Single.fromCallable { database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun convertEntityToTodo(list: List<TodoEntity>): MutableList<Todo> {
        val newList: MutableList<Todo> = ArrayList()
        list.forEach {
            newList.add(Todo(it.id, it.todo, it.done))
        }
        return newList
    }

    fun deleteTodo(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, todo.done)
        Single.fromCallable { database.todoDao().deleteTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        Snackbar.make(cl_main, todo.todoText + " have deleted", Snackbar.LENGTH_SHORT).show()
    }

    fun doneActivity() {
        val intent = Intent(this, DoneActivity::class.java)
        startActivity(intent)
    }
}
