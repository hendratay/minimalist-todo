package com.example.user.whattodo.fragment

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import com.example.user.whattodo.adapter.TaskAdapter
import com.example.user.whattodo.db.TodoEntity
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_task.*

class TaskFragment: Fragment() {

    private lateinit var adapter: TaskAdapter
    private var taskList: MutableList<Todo> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task,container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupButtonAdd()
        setupRecyclerView()
        setupBottomSheet()
        getTask()
    }

    private fun setupButtonAdd() {
        button_add_task.setOnClickListener {
            val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val dialog = BottomSheetDialog(activity as MainActivity)
            dialog.setContentView(bottomSheet)
            dialog.button_save_task.setOnClickListener {
                if(dialog.edit_text_task.text.isNotBlank()) {
                    (activity as MainActivity).database.todoDao().insertTodo(TodoEntity(dialog.edit_text_task.text.toString(), false, "Task", null))
                    getTask()
                }
                dialog.hide()
            }
            dialog.show()
        }
    }

    private fun setupRecyclerView() {
        rv_task.layoutManager = LinearLayoutManager(activity as MainActivity)
        adapter = TaskAdapter(taskList, { todo: Todo -> onItemChecked(todo) }, { todoList: List<Int> -> deleteTodo(todoList) })
        rv_task.adapter = adapter
    }

    private fun setupBottomSheet() {
        BottomSheetBehavior.from(bottom_sheet).setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }
        })
    }

    private fun getTask() {
        (activity as MainActivity).database.todoDao().getTodo("Task")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    taskList.clear()
                    for (todo: TodoEntity in it) {
                        taskList.add(Todo(todo.id, todo.todo, todo.done, todo.type, todo.dateTime))
                    }
                    adapter.notifyDataSetChanged()
                }
    }

    private fun onItemChecked(todo: Todo) {
        if(!rv_task.isComputingLayout) {
            if(todo.done) moveTodoToUndone(todo) else moveTodoToDone(todo)
        }
    }

    private fun moveTodoToDone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, true, todo.type, todo.date)
        Single.fromCallable { (activity as MainActivity).database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun moveTodoToUndone(todo: Todo) {
        val entity = TodoEntity(todo.todoId, todo.todoText, false, todo.type, todo.date)
        Single.fromCallable { (activity as MainActivity).database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun deleteTodo(list: List<Int>) {
        val backup: MutableList<TodoEntity> = ArrayList()
        list.forEach {
            val entity = TodoEntity(taskList[it].todoId, taskList[it].todoText, taskList[it].done, taskList[it].type, taskList[it].date)
            backup.add(entity)
            Single.fromCallable { (activity as MainActivity).database.todoDao().deleteTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        taskList.clear(); getTask(); adapter.notifyDataSetChanged()
        val snackBar = Snackbar.make(cl_task, "${list.size} item deleted", Snackbar.LENGTH_SHORT)
        snackBar.show()
        snackBar.setAction("UNDO") {
            backup.forEach {
                (activity as MainActivity).database.todoDao().insertTodo(it)
                getTask()
            }
        }
    }

}