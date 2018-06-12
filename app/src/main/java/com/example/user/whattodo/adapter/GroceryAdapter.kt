package com.example.user.whattodo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.grocery_list_item.view.*

class GroceryAdapter(private val groceryList: List<Todo>): RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    class GroceryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(todo: Todo, position: Int) {
            itemView.text_view_grocery.text = "${position + 1}. ${todo.todoText}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        return GroceryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grocery_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(groceryList[position], position)
    }

    override fun getItemCount() = groceryList.size

}