package com.example.user.whattodo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo

class HomeAdapter(private val todo: List<Todo>): RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(todo[position])
    }

    override fun getItemCount(): Int {
        return todo.size
    }

    inner class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(todo: Todo) {
        }
    }

}