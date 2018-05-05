package com.example.user.whattodo

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.db.TodoEntity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_done.*
import javax.inject.Inject

class DoneActivity : AppCompatActivity() {

    @Inject
    lateinit var database: TodoDatabase
    private lateinit var adapter: TodoAdapter

    private var TodoList: MutableList<Todo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)
        setSupportActionBar(tb_done)
        tb_done.setBackgroundColor(Color.WHITE)

        App.component.inject(this)

        rv_done_list.layoutManager = LinearLayoutManager(this)
        adapter = TodoAdapter(TodoList, ::onItemChecked, ::deleteTodo)
        rv_done_list.adapter = adapter

        refreshTodoRecycler()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.action_delete -> {
            deleteAllTodo()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    fun refreshTodoRecycler() {
        database.todoDao().getDoneTodo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    TodoList.clear()
                    for (todo: TodoEntity in it) {
                        TodoList.add(Todo(todo.id, todo.todo, todo.done))
                    }
                    adapter.notifyDataSetChanged()
                }
    }

    fun onItemChecked(todo: Todo) {
        TodoList.remove(todo)
        if(!rv_done_list.isComputingLayout) {
            adapter.notifyDataSetChanged()
            moveTodoToUndone(todo)
            Snackbar.make(ll_main, todo.todoText + " have not done yet", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun moveTodoToUndone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, false)
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
        val snackbar = Snackbar.make(ll_main, todoList.size.toString() + " have deleted", Snackbar.LENGTH_SHORT)
        snackbar.show()
        snackbar.setAction("UNDO", View.OnClickListener {
            backup.forEach{
                database.todoDao().insertTodo(it)
                refreshTodoRecycler()
            }
        })
    }

    fun deleteAllTodo() {
        val backup: MutableList<TodoEntity> = ArrayList()
        TodoList.forEach {
            val entity = TodoEntity(it.todoId, it.todoText, it.done)
            backup.add(entity)
            Single.fromCallable { database.todoDao().deleteTodo(entity)}
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        TodoList.clear()
        adapter.notifyDataSetChanged()
        val snackbar = Snackbar.make(ll_main, "You have deleted all done Todo", Snackbar.LENGTH_SHORT)
        snackbar.show()
        snackbar.setAction("UNDO", View.OnClickListener {
            backup.forEach {
                database.todoDao().insertTodo(it)
                refreshTodoRecycler()
            }
        })
    }

}