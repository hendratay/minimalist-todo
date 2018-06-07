package com.example.user.whattodo

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.user.whattodo.db.TodoEntity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_task.*

class TaskFragment: Fragment() {

    private lateinit var adapter: TodoAdapter
    private var TodoList: MutableList<Todo> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task,container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        getTodo()
        (activity as MainActivity).button_add.setOnClickListener {
            addTodoDialog()
        }
    }

    private fun setupRecyclerView() {
        rv_task.layoutManager = LinearLayoutManager(activity)
        adapter = TodoAdapter(TodoList, { todo: Todo -> onItemChecked(todo)}, { todoList: List<Todo> -> deleteTodo(todoList)})
        rv_task.adapter = adapter
    }

    private fun getTodo() {
        (activity as MainActivity).database.todoDao().getTodo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    TodoList.clear()
                    for (todo: TodoEntity in it) {
                        TodoList.add(Todo(todo.id, todo.todo, todo.done))
                    }
                    // For fixing java.lang.IndexOutOfBoundsException: Inconsistency detected
                    rv_task.recycledViewPool.clear()
                    adapter.notifyDataSetChanged()
                }
    }

    private fun onItemChecked(todo: Todo) {
        TodoList.remove(todo)
        if(!rv_task.isComputingLayout) {
            adapter.notifyDataSetChanged()
            moveTodoToDone(todo)
            Snackbar.make(cl_main, todo.todoText + " have done", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun moveTodoToDone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, true)
        Single.fromCallable { (activity as MainActivity).database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun deleteTodo(todoList: List<Todo>) {
        val backup: MutableList<TodoEntity> = ArrayList()
        todoList.forEach {
            val entity = TodoEntity(it.todoId, it.todoText, it.done)
            backup.add(entity)
            Single.fromCallable { (activity as MainActivity).database.todoDao().deleteTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        val snackbar = Snackbar.make(cl_main, todoList.size.toString() + " item deleted", Snackbar.LENGTH_SHORT)
        snackbar.show()
        snackbar.setAction("UNDO") {
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
        //alert.setView(todoEditText, 48, 48, 48, 0)

        alert.setPositiveButton("Add") {
            dialog, _ ->

            (activity as MainActivity).database.todoDao().insertTodo(TodoEntity(todoEditText.text.toString(), false))
            getTodo()

            dialog.dismiss()
        }
        alert.setNegativeButton("Cancel") {
            dialog, _ ->

            dialog.dismiss()
        }

        alert.show()
    }

}