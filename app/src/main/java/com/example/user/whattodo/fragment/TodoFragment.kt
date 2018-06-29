package com.example.user.whattodo.fragment

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.text.format.DateUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.db.TodoEntity
import com.example.user.whattodo.model.Todo
import com.example.user.whattodo.receiver.AlarmReceiver
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_add_grocery.view.*
import kotlinx.android.synthetic.main.dialog_add_reminder.view.*
import kotlinx.android.synthetic.main.dialog_add_task.view.*
import java.text.SimpleDateFormat
import java.util.*

open class TodoFragment: Fragment() {

    private val backup: MutableList<TodoEntity> = ArrayList()
    private var notificationId = 0
    private lateinit var alarmManager: AlarmManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_todo,container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        addTodoDialog()
    }

    // Todo move this to MainActivity
    private fun addTodoDialog() {
        (activity as MainActivity).view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                (activity as MainActivity).fab_add_todo.setOnClickListener {
                    when(position) {
                        0 -> addTaskDialog()
                        1 -> addReminderDialog()
                        2 -> addGroceryDialog()
                    }
                }
            }

            override fun onPageSelected(position: Int) {
            }
        })
    }

    private fun addTaskDialog(){
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

    private fun addReminderDialog() {
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
        view.text_view_time.text = DateUtils.getRelativeTimeSpanString(now.timeInMillis, now.timeInMillis, DateUtils.MINUTE_IN_MILLIS)
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
                view.text_view_time.text = DateUtils.getRelativeTimeSpanString(calendar.timeInMillis, now.timeInMillis, DateUtils.MINUTE_IN_MILLIS)
            }, hour, minute, false).show()
        }
        view.button_add_reminder.setOnClickListener {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/d HH:mm" ).parse("$selectedDate $selectedTime")
            if(view.edit_text_reminder.text.isNotBlank()) {
                insertTodo(TodoEntity(view.edit_text_reminder.text.toString(), false, "Reminder", simpleDateFormat))
//                setReminder(simpleDateFormat.time, view.edit_text_reminder.text.toString())
                dialog.dismiss()
            }
        }
        dialog.window.attributes.gravity = Gravity.BOTTOM
        dialog.show()
    }

    private fun addGroceryDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_grocery, null)
        val dialog = AlertDialog.Builder(activity as MainActivity, R.style.DialogTheme).setView(view).create()
        view.button_add_grocery.setOnClickListener {
            if(view.edit_text_grocery.text.isNotBlank()) {
                insertTodo(TodoEntity(view.edit_text_grocery.text.toString(), false, "Grocery", null))
                dialog.dismiss()
            }
        }
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.show()
    }

    open fun setupRecyclerView() {
    }

    fun getTodo(type: String): Flowable<List<TodoEntity>> {
        return (activity as MainActivity).database.todoDao().getTodo(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun insertTodo(todoEntity: TodoEntity)  {
        (activity as MainActivity).database.todoDao().insertTodo(todoEntity)
    }

    fun updateTodo(todo: Todo, done: Boolean) {
        val entity = TodoEntity(todo.todoId, todo.todoText, done, todo.type, todo.date)
        Single.fromCallable { (activity as MainActivity).database.todoDao().updateTodo(entity) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun deleteTodo(selected: List<Int>, todoList: List<Todo>) {
        backup.clear()
        selected.forEach {
            val entity = TodoEntity(todoList[it].todoId, todoList[it].todoText, todoList[it].done, todoList[it].type, todoList[it].date)
            backup.add(entity)
            Single.fromCallable { (activity as MainActivity).database.todoDao().deleteTodo(entity) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
    }

    fun undoDeleteTodo() {
        backup.forEach {
            (activity as MainActivity).database.todoDao().insertTodo(it)
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