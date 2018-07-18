package com.example.user.whattodo.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.whattodo.R
import com.example.user.whattodo.model.GeneralItem
import com.example.user.whattodo.model.HeaderItem
import com.example.user.whattodo.model.ListItem
import kotlinx.android.synthetic.main.item_general.view.*
import kotlinx.android.synthetic.main.item_header.view.*

class HomeAdapter(private val todo: List<ListItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        when(viewType) {
            ListItem.TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_header, parent, false)
                viewHolder = HeaderViewHolder(view)
            }
            ListItem.TYPE_GENERAL -> {
                val view = inflater.inflate(R.layout.item_general, parent, false)
                viewHolder = GeneralViewHolder(view)
            }
            ListItem.TYPE_FOOTER -> {
                val view = inflater.inflate(R.layout.item_footer, parent, false)
                viewHolder = FooterViewHolder(view)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            ListItem.TYPE_HEADER -> {
                val typeItem = todo[position] as HeaderItem
                val headerViewHolder = holder as HeaderViewHolder
                headerViewHolder.typeTextView.text = typeItem.type
            }
            ListItem.TYPE_GENERAL -> {
                val todoItem = todo[position] as GeneralItem
                val generalViewHolder = holder as GeneralViewHolder
                generalViewHolder.todoTextView.text = todoItem.todo.todoText
            }
        }
    }

    override fun getItemCount(): Int = todo.size

    override fun getItemViewType(position: Int) = todo[position].getType()

    inner class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var typeTextView = itemView.item_header_todo_type
    }

    inner class GeneralViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var todoTextView = itemView.item_general_todo
    }

    inner class FooterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }

}