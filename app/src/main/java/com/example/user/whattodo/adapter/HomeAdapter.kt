package com.example.user.whattodo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.R

class HomeAdapter(private val todoType: List<String>,
                  private val todoAll: List<Int>,
                  private val todoDone: List<Int>):
        RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        if (todoType.isNotEmpty() && todoAll.isNotEmpty() && todoDone.isNotEmpty()) {
            holder.bind(todoType[position], todoAll[position], todoDone[position])
        }
    }

    override fun getItemCount(): Int {
        return todoType.size
    }

    inner class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(type: String, allTodo: Int, todoDone: Int) {
        }
    }

}