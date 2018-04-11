package com.example.user.whattodo

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
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

    private var TodoList: MutableList<Todo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done)
        setSupportActionBar(tb_done)

        App.component.inject(this)

        rv_done_list.layoutManager = LinearLayoutManager(this)
        rv_done_list.adapter = TodoAdapter(TodoList, ::onItemChecked, ::deleteTodo)

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
                .subscribe{
                    TodoList.clear()
                    it.forEach {
                        TodoList.add(Todo(it.id, it.todo, it.done))
                    }
                    rv_done_list.adapter = TodoAdapter(TodoList, ::onItemChecked, ::deleteTodo)
                }
    }

    fun onItemChecked(todo: Todo) {
        TodoList.remove(todo)
        rv_done_list.adapter = TodoAdapter(TodoList, ::onItemChecked, ::deleteTodo)
        moveTodoToUndone(todo)
        Snackbar.make(ll_main, todo.todoText + " have not done yet", Snackbar.LENGTH_SHORT).show()
    }

    fun moveTodoToUndone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, false)
        Single.fromCallable { database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun deleteTodo(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, todo.done)
        Single.fromCallable { database.todoDao().deleteTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        Snackbar.make(ll_main, todo.todoText + " have deleted", Snackbar.LENGTH_SHORT).show()
    }

    fun deleteAllTodo() {
        TodoList.forEach {
            val entity = TodoEntity(it.todoId, it.todoText, it.done)
            Single.fromCallable { database.todoDao().deleteTodo(entity)}
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        TodoList.clear()
        rv_done_list.adapter = TodoAdapter(TodoList, ::onItemChecked, ::deleteTodo)
        Snackbar.make(ll_main, "You have delete all done Todo", Snackbar.LENGTH_SHORT).show()
    }

}