package com.example.user.whattodo.adapter

import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.reminder_list_item.view.*

class ReminderAdapter(private val reminderList: List<Todo>,
                      private val changeListener: (Todo) -> Unit,
                      private val deleteListener: (List<Int>) -> Unit):
        RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    private var actionMode: ActionMode? = null
    private var multiSelect: Boolean = false
    private var selectedItems: HashMap<Int, Todo> = hashMapOf()

    private var actionModeCallbacks: ActionMode.Callback = object: ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            multiSelect = true
            menu?.add("Delete")
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            deleteListener(selectedItems.keys.toList())
            mode?.finish()
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            multiSelect = false
            selectedItems.clear()
            notifyDataSetChanged()
        }
    }

    inner class ReminderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(todo: Todo, changeListener: (Todo) -> Unit) {
            itemView.text_view_reminder_date.text = todo.date.toString()
            itemView.text_view_reminder.text = todo.todoText
            itemView.check_box_reminder.isChecked = todo.done
            itemView.text_view_reminder.text = todo.todoText
            itemView.text_view_reminder.paintFlags = if(todo.done) (Paint.STRIKE_THRU_TEXT_FLAG) else 0
            itemView.check_box_reminder.setOnCheckedChangeListener { _, _ ->
                changeListener(todo)
            }
            update(adapterPosition)
        }

        private fun selectItem(item: Int) {
            if(multiSelect) {
                if (selectedItems.containsKey(item)) {
                    selectedItems.remove(item)
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    selectedItems[item] = reminderList[item]
                    itemView.setBackgroundColor(Color.LTGRAY)
                }
            }
        }

        private fun update(value: Int) {
            if(selectedItems.containsKey(value)) {
                itemView.setBackgroundColor(Color.LTGRAY)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
            itemView.setOnLongClickListener {
                actionMode = (it.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
                selectItem(value)
                true
            }
            itemView.setOnClickListener {
                if(actionMode == null) itemView.check_box_reminder.isChecked = !itemView.check_box_reminder.isChecked else selectItem(value)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.reminder_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminderList[position], changeListener)
    }

    override fun getItemCount() = reminderList.size

}
