package com.example.user.whattodo.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.adapter.ReminderAdapter
import com.example.user.whattodo.db.TodoEntity
import com.example.user.whattodo.model.Todo
import com.example.user.whattodo.utils.HeaderDecoration
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_add_reminder.view.*
import kotlinx.android.synthetic.main.fragment_reminder.*
import java.text.SimpleDateFormat
import java.util.*

class ReminderFragment: Fragment() {

    private lateinit var adapter: ReminderAdapter
    private var reminderList: MutableList<Todo> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminder, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupButtonAdd()
        setupRecyclerView()
        getReminder()
    }

    private fun setupButtonAdd() {
        button_add_reminder.setOnClickListener {
            addReminderDialog()
        }
    }

    private fun setupRecyclerView() {
        rv_reminder.layoutManager = LinearLayoutManager(activity as MainActivity)
        rv_reminder.addItemDecoration(HeaderDecoration((activity as MainActivity), rv_reminder, R.layout.header_item, "Remind Me"))
        adapter = ReminderAdapter(reminderList, { onItemChecked(it) }, { deleteTodo(it) })
        rv_reminder.adapter = adapter
    }

    private fun addReminderDialog() {
        val alert = AlertDialog.Builder(activity as MainActivity)
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minutes = now.get(Calendar.MINUTE)
        val view = (activity as MainActivity).layoutInflater.inflate(R.layout.dialog_add_reminder, null)
        view.text_view_date.text = "$year / $month / $day"
        view.text_view_time.text = "$hour : $minutes"
        alert.setView(view)
                .setPositiveButton("OK") { _, _ ->
                    val date = view.text_view_date.text
                    val time = view.text_view_time.text
                    val simpleDateFormat = SimpleDateFormat("yyyy / MM / d HH : mm" ).parse("$date $time")
                    val todoEntity = TodoEntity(view.edit_text_reminder.text.toString(), false, "Reminder", simpleDateFormat)
                    (activity as MainActivity).database.todoDao().insertTodo(todoEntity)
                    getReminder()
                }
        view.text_view_date.setOnClickListener {
            val datePicker = DatePickerDialog(activity, { _, thisyear, thismonth, thisdayOfMonth ->
                view.text_view_date.text = "$thisyear / $thismonth / $thisdayOfMonth"
            }, year, month, day)
            datePicker.show()
        }
        view.text_view_time.setOnClickListener {
            val timePicker = TimePickerDialog(activity, { _, hourOfDay, minute ->
                view.text_view_time.text = "$hourOfDay : $minute"
            }, hour, minutes, false)
            timePicker.show()
        }
        alert.show()
    }

    private fun getReminder() {
        (activity as MainActivity).database.todoDao().getTodo("Reminder")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    reminderList.clear()
                    for (todo: TodoEntity in it) {
                        reminderList.add(Todo(todo.id, todo.todo, todo.done, todo.type, todo.dateTime))
                    }
                    adapter.notifyDataSetChanged()
                }
    }

    private fun onItemChecked(todo: Todo) {
        if(!rv_reminder.isComputingLayout) {
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
            val entity = TodoEntity(reminderList[it].todoId, reminderList[it].todoText, reminderList[it].done, reminderList[it].type, reminderList[it].date)
            backup.add(entity)
            Single.fromCallable { (activity as MainActivity).database.todoDao().deleteTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
        reminderList.clear(); getReminder(); adapter.notifyDataSetChanged()
        val snackBar = Snackbar.make(cl_reminder, "${list.size} item deleted", Snackbar.LENGTH_SHORT)
        snackBar.show()
        snackBar.setAction("UNDO") {
            backup.forEach {
                (activity as MainActivity).database.todoDao().insertTodo(it)
                getReminder()
            }
        }
    }

}
