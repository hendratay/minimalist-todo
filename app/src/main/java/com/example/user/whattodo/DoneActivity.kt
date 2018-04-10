package com.example.user.whattodo

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.db.TodoEntity
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

        refreshTodoRecycler()
    }

    fun refreshTodoRecycler() {
        database.todoDao().getDoneTodo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    TodoList = convertEntityToTodo(it)
                    rv_done_list.adapter = TodoAdapter(TodoList, null)
                }
    }

    fun convertEntityToTodo(list: List<TodoEntity>): MutableList<Todo> {
        val newList: MutableList<Todo> = ArrayList()
        list.forEach {
            newList.add(Todo(it.id, it.todo, it.done))
        }
        return newList
    }

}