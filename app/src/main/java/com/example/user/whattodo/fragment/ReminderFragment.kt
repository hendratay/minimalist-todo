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
import android.text.format.DateUtils
import android.view.Gravity
import android.view.View
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

    private lateinit var adapter: ReminderAdapter
    private var reminderList: MutableList<Todo> = ArrayList()
    private var notificationId = 0
    private lateinit var alarmManager: AlarmManager

    override fun onStart() {
        super.onStart()
        getReminder()
    }

    override fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = ReminderAdapter(reminderList, { onItemChecked(it) }, { onItemDeleted(it) })
        recycler_view.adapter = adapter
    }

    fun addReminderDialog() {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        var selectedDate = "$year/${month + 1}/$day"
        var selectedTime = "$year:$minute"
        val view = layoutInflater.inflate(R.layout.dialog_add_reminder, null)
        val dialog = AlertDialog.Builder(activity as MainActivity, R.style.DialogTheme).setView(view).create()
        view.text_view_date.text = DateUtils.getRelativeTimeSpanString(now.timeInMillis, now.timeInMillis, DateUtils.DAY_IN_MILLIS)
        view.text_view_time.text = getString(R.string.date_utils_time_now)
        view.text_view_date.setOnClickListener {
            DatePickerDialog(activity, { _, thisyear, thismonth, thisdayOfMonth ->
                selectedDate = "$thisyear/$thismonth/$thisdayOfMonth"
                val calendar = GregorianCalendar(thisyear, thismonth, thisdayOfMonth)
                view.text_view_date.text = DateUtils.getRelativeTimeSpanString(calendar.timeInMillis, now.timeInMillis, DateUtils.DAY_IN_MILLIS)
            }, year, month, day).show()
        }
        view.text_view_time.setOnClickListener {
            TimePickerDialog(activity, {_, thisHourOfDay, thisMinute ->
                selectedTime = "$thisHourOfDay:$thisMinute"
                val calendar = GregorianCalendar(year, month, day, thisHourOfDay, thisMinute)
                view.text_view_time.text = if(thisHourOfDay == hour && thisMinute == minute) getString(R.string.date_utils_time_now)
                else DateUtils.getRelativeTimeSpanString(calendar.timeInMillis, now.timeInMillis, DateUtils.MINUTE_IN_MILLIS)
            }, hour, minute, false).show()
        }
        view.button_add_reminder.setOnClickListener {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/d HH:mm" ).parse("$selectedDate $selectedTime")
            if(view.edit_text_reminder.text.isNotBlank()) {
                insertTodo(TodoEntity(view.edit_text_reminder.text.toString(), false, "Reminder", simpleDateFormat))
                setReminder(simpleDateFormat.time, view.edit_text_reminder.text.toString())
                dialog.dismiss()
            }
        }
        dialog.window.attributes.gravity = Gravity.BOTTOM
        dialog.show()
    }

    private fun getReminder() {
        getTodo("Reminder")
                .subscribe {
                    reminderList.clear()
                    it.forEach { reminderList.add(Todo(it.id, it.todo, it.done, it.type, it.dateTime)) }
                    emptyView()
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

    override fun destroyActionCallback() {
        adapter.deleteActionMode.actionMode?.finish()
        adapter.deleteActionMode.actionMode = null
    }

    private fun emptyView() {
        empty_todo.setImageResource(R.drawable.empty_deadline)
        if(reminderList.isEmpty()) {
            empty_todo.visibility = View.VISIBLE
        } else {
            empty_todo.visibility = View.GONE
        }
    }

}
