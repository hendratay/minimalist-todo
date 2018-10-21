package com.example.user.whattodo.adapter

import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.user.whattodo.utils.DeleteActionModeCallback
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.item_reminder.view.*
import java.text.SimpleDateFormat

class ReminderAdapter(private val reminderList: List<Todo>,
                      private val changeListener: (Todo) -> Unit,
                      deleteListener: (List<Int>) -> Unit):
        RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    var deleteActionMode = DeleteActionModeCallback(null, false, hashMapOf(), deleteListener, this as RecyclerView.Adapter<RecyclerView.ViewHolder>)

    inner class ReminderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(todo: Todo, changeListener: (Todo) -> Unit) {
            itemView.check_box_reminder.isChecked = todo.done
            itemView.text_view_reminder.text = todo.todoText.capitalize()
            val sdf = SimpleDateFormat("EEEE, d MMM yyyy, h a")
            itemView.text_view_reminder_date.text = sdf.format(todo.date)
            itemView.text_view_reminder.paintFlags = if(todo.done) (Paint.STRIKE_THRU_TEXT_FLAG) else 0
            itemView.text_view_reminder_date.paintFlags = if(todo.done) (Paint.STRIKE_THRU_TEXT_FLAG) else 0
            itemView.check_box_reminder.setOnCheckedChangeListener { _, _ ->
                changeListener(todo)
            }
            update(adapterPosition)
        }

        private fun selectItem(item: Int) {
            if(deleteActionMode.multiSelect) {
                if (deleteActionMode.selectedItems.containsKey(item)) {
                    deleteActionMode.selectedItems.remove(item)
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    deleteActionMode.selectedItems[item] = reminderList[item]
                    itemView.setBackgroundColor(Color.LTGRAY)
                }
            }
        }

        private fun update(value: Int) {
            if(deleteActionMode.selectedItems.containsKey(value)) {
                itemView.setBackgroundColor(Color.LTGRAY)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
            itemView.setOnLongClickListener {
                deleteActionMode.actionMode = (it.context as AppCompatActivity).startSupportActionMode(deleteActionMode)
                selectItem(value)
                true
            }
            itemView.setOnClickListener {
                if(deleteActionMode.actionMode == null) itemView.check_box_reminder.isChecked = !itemView.check_box_reminder.isChecked else selectItem(value)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false))
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminderList[position], changeListener)
    }

    override fun getItemCount() = reminderList.size

}
