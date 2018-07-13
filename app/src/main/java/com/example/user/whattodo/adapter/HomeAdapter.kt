package com.example.user.whattodo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.R
import kotlinx.android.synthetic.main.home_list_item.view.*

class HomeAdapter(private val type: List<String>,
                  private val count: List<Int>,
                  private val done: List<Int>): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var todoTypeImageView = itemView.todo_type_image
        var todoTypeTextView = itemView.todo_type_text
        var todoTypeTodo = itemView.todo_type_todo
        var todoTypeComplete = itemView.todo_type_todo_complete
        var todoTypeInComplete = itemView.todo_type_todo_incomplete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        when(type[position]) {
            "Task" -> holder.todoTypeImageView.setImageResource(R.drawable.ic_check_white_24dp)
            "Reminder" -> holder.todoTypeImageView.setImageResource(R.drawable.ic_today_white_24dp)
            "Grocery" -> holder.todoTypeImageView.setImageResource(R.drawable.ic_shopping_cart_white_24dp)
        }
        holder.todoTypeTextView.text = type[position]
        if(count.isNotEmpty()) holder.todoTypeTodo.text = when(position){
            0 -> "Tasks: ${count[position]}"
            1 -> "Reminders: ${count[position]}"
            2 -> "Groceries: ${count[position]}"
            else -> ""
        }
        if(done.isNotEmpty()) holder.todoTypeComplete.text = done[position].toString()
        if(count.isNotEmpty() && done.isNotEmpty()) holder.todoTypeInComplete.text = (count[position] - done[position]).toString()
    }

    override fun getItemCount(): Int = type.size

}