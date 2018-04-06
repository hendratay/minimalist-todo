package com.example.user.whattodo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import kotlinx.android.synthetic.main.todo_list_item.view.*;

class TodoAdapter(val items : ArrayList<Todo>) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvTodo = itemView.tv_todo
        val cbTodo = itemView.cb_todo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.tvTodo?.text = items[position].todoText
        holder?.cbTodo?.isChecked = items[position].done
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
