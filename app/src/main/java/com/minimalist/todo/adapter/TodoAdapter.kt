package com.minimalist.todo.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minimalist.todo.R
import com.minimalist.todo.model.Todo
import kotlinx.android.synthetic.main.item_todo.view.*

class TodoAdapter(private val items: MutableList<Todo>,
                  private val changeListener: ((Todo) -> Unit)?,
                  private val deleteTodoListener: (Todo) -> Unit) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(todo: Todo, changeListener: ((Todo) -> Unit)?, deleteTodoListener: (Todo) -> Unit) {
            itemView.check_box_task.isChecked = todo.done
            if (todo.done) {
                itemView.text_view_task.text = todo.todoText
                itemView.text_view_task.paintFlags = (Paint.STRIKE_THRU_TEXT_FLAG)
                itemView.image_button_delete.visibility = View.VISIBLE
            } else {
                itemView.text_view_task.text = todo.todoText
                itemView.text_view_task.paintFlags = 0
                itemView.image_button_delete.visibility = View.GONE
            }
            itemView.check_box_task.setOnCheckedChangeListener { _, _ ->
                changeListener?.invoke(todo)
            }
            itemView.text_view_task.setOnClickListener {
                itemView.check_box_task.isChecked = !todo.done
            }
            itemView.image_button_delete.setOnClickListener {
                deleteTodoListener.invoke(todo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], changeListener, deleteTodoListener)
    }

    override fun getItemCount() = items.size
}
