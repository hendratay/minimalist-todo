package com.example.user.whattodo.adapter

import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.user.whattodo.utils.DeleteActionModeCallback
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.item_grocery.view.*

class GroceryAdapter(private val groceryList: List<Todo>,
                     private val changeListener: (Todo) -> Unit,
                     deleteListener: (List<Int>) -> Unit):
        RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    var deleteActionMode = DeleteActionModeCallback(null, false, hashMapOf(), deleteListener, this as RecyclerView.Adapter<RecyclerView.ViewHolder>)

    inner class GroceryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(todo: Todo, position: Int, changeListener: (Todo) -> Unit) {
            itemView.check_box_grocery.isChecked = todo.done
            itemView.text_view_grocery.text = "${position + 1}. ${todo.todoText.capitalize()}"
            itemView.text_view_grocery.paintFlags = if(todo.done) (Paint.STRIKE_THRU_TEXT_FLAG) else 0
            itemView.check_box_grocery.setOnCheckedChangeListener { _, _ ->
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
                    deleteActionMode.selectedItems[item] = groceryList[item]
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
                if(deleteActionMode.actionMode == null) itemView.check_box_grocery.isChecked = !itemView.check_box_grocery.isChecked else selectItem(value)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        return GroceryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_grocery, parent, false))
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(groceryList[position], position, changeListener)
    }

    override fun getItemCount() = groceryList.size

}