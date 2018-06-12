package com.example.user.whattodo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.reminder_list_item.view.*

class ReminderAdapter(val reminderList: MutableList<Todo>): RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(todo: Todo) {
            itemView.text_view_reminder_date.text = todo.date.toString()
            itemView.text_view_reminder.text = todo.todoText
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.reminder_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminderList[position])
    }

    override fun getItemCount() = reminderList.size

}
