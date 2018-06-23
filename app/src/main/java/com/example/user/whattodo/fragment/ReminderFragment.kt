package com.example.user.whattodo.fragment

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.receiver.AlarmReceiver
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.adapter.ReminderAdapter
import com.example.user.whattodo.db.TodoEntity
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.dialog_add_reminder.view.*
import kotlinx.android.synthetic.main.fragment_todo.*
import java.text.SimpleDateFormat
import java.util.*

class ReminderFragment: TodoFragment() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var adapter: ReminderAdapter
    private var reminderList: MutableList<Todo> = ArrayList()
    private var notificationId = 0

    override fun onStart() {
        super.onStart()
        getReminder()
    }

    override fun addTodoDialog() {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minutes = now.get(Calendar.MINUTE)
        val dialog = AlertDialog.Builder(activity as MainActivity)
        val view = (activity as MainActivity).layoutInflater.inflate(R.layout.dialog_add_reminder, null)
        view.text_view_date.text = "$year / ${month + 1} / $day"
        view.text_view_time.text = "$hour : $minutes"
        dialog.setView(view)
                .setPositiveButton("Add") { _, _ ->
                    val date = view.text_view_date.text
                    val time = view.text_view_time.text
                    val simpleDateFormat = SimpleDateFormat("yyyy / MM / d HH : mm" ).parse("$date $time")
                    insertTodo(TodoEntity(view.edit_text_reminder.text.toString(), false, "Reminder", simpleDateFormat))
                    setReminder(simpleDateFormat.time, view.edit_text_reminder.text.toString())
                }
        view.text_view_date.setOnClickListener {
            DatePickerDialog(activity, { _, thisyear, thismonth, thisdayOfMonth ->
                view.text_view_date.text = "$thisyear / ${thismonth + 1} / $thisdayOfMonth"
            }, year, month, day).show()
        }
        view.text_view_time.setOnClickListener {
            TimePickerDialog(activity, { _, hourOfDay, minute ->
                view.text_view_time.text = "$hourOfDay : $minute"
            }, hour, minutes, false).show()
        }
        dialog.show()
    }

    override fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = ReminderAdapter(reminderList, { onItemChecked(it) }, { onItemDeleted(it) })
        recycler_view.adapter = adapter
    }

    private fun getReminder() {
        getTodo("Reminder")
                .subscribe {
                    reminderList.clear()
                    it.forEach { reminderList.add(Todo(it.id, it.todo, it.done, it.type, it.dateTime)) }
                    adapter.notifyDataSetChanged()
                }
    }

    private fun onItemChecked(todo: Todo) {
        if(!recycler_view.isComputingLayout) {
            if(todo.done) updateTodo(todo, false) else updateTodo(todo, true)
        }
    }

    private fun onItemDeleted(selected: List<Int>) {
        deleteTodo(selected, reminderList)
        getReminder()
        val snackBar = Snackbar.make(coordinator_layout, "${selected.size} item deleted", Snackbar.LENGTH_SHORT)
        snackBar.show()
        snackBar.setAction("UNDO") {
            undoDeleteTodo()
            getReminder()
        }
    }

    private fun setReminder(time: Long, reminder: String) {
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, time,
                PendingIntent.getBroadcast(activity,
                        0,
                        Intent(activity, AlarmReceiver::class.java).apply {
                            putExtra("notificationId", ++notificationId)
                            putExtra("reminder", reminder)}, PendingIntent.FLAG_CANCEL_CURRENT)
                )

    }

}
