package com.example.user.whattodo.fragment

import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import com.example.user.whattodo.activity.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import com.example.user.whattodo.adapter.TaskAdapter
import com.example.user.whattodo.db.TodoEntity
import com.example.user.whattodo.utils.snackBar
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.dialog_add_task.view.*
import kotlinx.android.synthetic.main.fragment_todo.*

class TaskFragment: TodoFragment() {

    private lateinit var adapter: TaskAdapter
    private var taskList: MutableList<Todo> = ArrayList()

    var compositeDisposable = CompositeDisposable()

    override fun onStart() {
        super.onStart()
        getTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(taskList, { todo: Todo -> onItemChecked(todo) }, { todoList: List<Int> -> onItemDeleted(todoList) })
        recycler_view.adapter = adapter
    }

    fun addTaskDialog(){
        val view = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val dialog = AlertDialog.Builder(activity as MainActivity, R.style.DialogTheme).setView(view).create()
        view.button_add_task.setOnClickListener {
            if(view.edit_text_task.text.isNotBlank()) {
                insertTodo(TodoEntity(view.edit_text_task.text.toString(), false, "Task", null))
                dialog.dismiss()
            }
        }
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    private fun getTask() {
        val disposable = getTodo("Task")
                .subscribe {
                    if (view != null) {
                        taskList.clear()
                        it.forEach { taskList.add(Todo(it.id, it.todo, it.done, it.type, it.dateTime)) }
                        emptyView()
                        adapter.notifyDataSetChanged()
                    }
                }
        compositeDisposable.add(disposable)
    }

    private fun onItemChecked(todo: Todo) {
        if(!recycler_view.isComputingLayout) {
            if(todo.done) updateTodo(todo, false) else updateTodo(todo, true)
        }
    }

    private fun onItemDeleted(selected: List<Int>) {
        deleteTodo(selected, taskList)
        getTask()
        snackBar(coordinator_layout, "${selected.size} item deleted", "UNDO") {
            undoDeleteTodo()
            getTask()
        }
    }

    override fun destroyActionCallback() {
        adapter.deleteActionMode.actionMode?.finish()
        adapter.deleteActionMode.actionMode = null
    }

    private fun emptyView() {
        empty_todo.setImageResource(R.drawable.empty_thinking)
        if(taskList.isEmpty()) {
            empty_todo.visibility = View.VISIBLE
        } else {
            empty_todo.visibility = View.GONE
        }
    }

}