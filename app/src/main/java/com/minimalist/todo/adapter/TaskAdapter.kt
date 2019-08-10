package com.minimalist.todo.adapter

import android.graphics.Color
import android.graphics.Paint
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.minimalist.todo.utils.DeleteActionModeCallback
import com.minimalist.todo.R
import com.minimalist.todo.model.Todo
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter(val items : MutableList<Todo>,
                  private val changeListener: ((Todo) -> Unit)?,
                  deleteTodoListener: (List<Int>) -> Unit) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    var deleteActionMode = DeleteActionModeCallback(null, false, hashMapOf(), deleteTodoListener, this as RecyclerView.Adapter<RecyclerView.ViewHolder>)

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(todo: Todo, changeListener: ((Todo) -> Unit)?) {
            itemView.check_box_task.isChecked = todo.done
            if(todo.done) {
                itemView.text_view_task.text = todo.todoText.capitalize()
                itemView.text_view_task.paintFlags = (Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                itemView.text_view_task.text = todo.todoText.capitalize()
                itemView.text_view_task.paintFlags = 0
            }
            itemView.check_box_task.setOnCheckedChangeListener { _, _ ->
                changeListener?.invoke(todo)
            }
            update(adapterPosition)
        }

        private fun selectItem(item: Int) {
            if(deleteActionMode.multiSelect) {
                if (deleteActionMode.selectedItems.containsKey(item)) {
                    deleteActionMode.selectedItems.remove(item)
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    deleteActionMode.selectedItems[item] = items[item]
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
                if(deleteActionMode.actionMode == null) itemView.check_box_task.isChecked = !itemView.check_box_task.isChecked else selectItem(value)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], changeListener)
    }

    override fun getItemCount() = items.size
}
