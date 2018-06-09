package com.example.user.whattodo.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import com.example.user.whattodo.adapter.TodoAdapter
import com.example.user.whattodo.db.TodoEntity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_task.*

class TaskFragment: Fragment() {

    private lateinit var adapter: TodoAdapter
    private var todoList: MutableList<Todo> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task,container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupButtonAdd()
        setupRecyclerView()
        getTodo()
    }

    private fun setupButtonAdd() {
        button_add_task.text = "Add Task"
        button_add_task.setOnClickListener {
            addTodoDialog()
        }
    }

    private fun setupRecyclerView() {
        rv_task.layoutManager = LinearLayoutManager(activity as MainActivity)
        adapter = TodoAdapter(todoList, { todo: Todo -> onItemChecked(todo) }, { todoList: List<Todo> -> deleteTodo(todoList) })
        rv_task.adapter = adapter
    }

    private fun getTodo() {
        (activity as MainActivity).database.todoDao().getTodo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    todoList.clear()
                    for (todo: TodoEntity in it) {
                        todoList.add(Todo(todo.id, todo.todo, todo.done))
                    }
                    // For fixing java.lang.IndexOutOfBoundsException: Inconsistency detected
                    rv_task.recycledViewPool.clear()
                    adapter.notifyDataSetChanged()
                }
    }

    private fun onItemChecked(todo: Todo) {
        if(!rv_task.isComputingLayout) {
            if(todo.done) moveTodoToUndone(todo) else moveTodoToDone(todo)
            todoList.clear()
            getTodo()
            adapter.notifyDataSetChanged()
        }
    }

    private fun moveTodoToDone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, true)
        Single.fromCallable { (activity as MainActivity).database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun moveTodoToUndone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, false)
        Single.fromCallable { (activity as MainActivity).database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun deleteTodo(list: List<Todo>) {
        val backup: MutableList<TodoEntity> = ArrayList()
        list.forEach {
            val entity = TodoEntity(it.todoId, it.todoText, it.done)
            backup.add(entity)
            Single.fromCallable { (activity as MainActivity).database.todoDao().deleteTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        todoList.clear(); getTodo(); adapter.notifyDataSetChanged()
        val snackBar = Snackbar.make(cl_task, "${list.size} item deleted", Snackbar.LENGTH_SHORT)
        snackBar.show()
        snackBar.setAction("UNDO") {
            backup.forEach {
                (activity as MainActivity).database.todoDao().insertTodo(it)
                getTodo()
            }
        }
    }

    private fun addTodoDialog() {
        val alert = AlertDialog.Builder(activity)
        val todoEditText = EditText(activity)
        todoEditText.hint = "Enter Todo"

        alert.setTitle("Add New Todo")
        // TODO: Use custom layout
        alert.setView(todoEditText)

        alert.setPositiveButton("Add") { dialog, _ ->
            (activity as MainActivity).database.todoDao().insertTodo(TodoEntity(todoEditText.text.toString(), false))
            getTodo()
            dialog.dismiss()
        }
        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

}