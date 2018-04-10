package com.example.user.whattodo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import com.example.user.whattodo.db.TodoDatabase
import com.example.user.whattodo.db.TodoEntity
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

        App.component.inject(this)

        fab_add_todo.setOnClickListener{
            addTodoDialog()
        }

        rv_todo_list.layoutManager = LinearLayoutManager(this)

        refreshTodoRecycler()
    }

    fun addTodoDialog() {
        val alert = AlertDialog.Builder(this)
        val todoEditText = EditText(this)

        alert.setTitle("Enter TODO")
        alert.setMessage("Add a new Todo")
        alert.setView(todoEditText)

        alert.setPositiveButton("Add") {
            dialog, which ->

            database.todoDao().insertTodo(TodoEntity(todoEditText.text.toString(), false))
            refreshTodoRecycler()

            dialog.dismiss()
        }
        alert.setNegativeButton("Cancel") {
            dialog, which ->

            dialog.dismiss()
        }

        alert.show()
    }

    fun refreshTodoRecycler() {
        database.todoDao().getAllTodo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    TodoList = convertEntityToTodo(it)
                    rv_todo_list.adapter = TodoAdapter(TodoList)
                }
    }

    fun convertEntityToTodo(list: List<TodoEntity>): MutableList<Todo> {
        val newList: MutableList<Todo> = ArrayList()
        list.forEach {
            newList.add(Todo(it.todo, it.done))
        }
        return newList
    }
}
