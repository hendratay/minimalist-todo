package com.example.user.whattodo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*;
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var database : TodoDatabase

    val TodoList = ArrayList<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_add_todo.setOnClickListener{
            addTodoDialog()
        }

        rv_todo_list.layoutManager = LinearLayoutManager(this)
        rv_todo_list.adapter = TodoAdapter(TodoList);

    }

    fun addTodoDialog() {
        val alert = AlertDialog.Builder(this)
        val todoEditText = EditText(this)

        alert.setTitle("Enter TODO")
        alert.setMessage("Add a new Todo")
        alert.setView(todoEditText)

        alert.setPositiveButton("Add") {
            dialog, which ->

            //database.todoDao().insertTodo(1, todoEditText.text.toString(), false)
            TodoList.add(Todo(todoEditText.text.toString(), false))
            rv_todo_list.adapter.notifyDataSetChanged()

            dialog.dismiss()
        }
        alert.setNegativeButton("Cancel") {
            dialog, which ->

            dialog.dismiss()
        }

        alert.show()
    }
}
