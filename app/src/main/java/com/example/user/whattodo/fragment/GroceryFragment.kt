package com.example.user.whattodo.fragment

import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.adapter.GroceryAdapter
import com.example.user.whattodo.db.TodoEntity
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.dialog_add_grocery.view.*
import kotlinx.android.synthetic.main.fragment_todo.*

class GroceryFragment: TodoFragment() {

    private lateinit var adapter: GroceryAdapter
    private var groceryList: MutableList<Todo> = ArrayList()

    override fun onStart() {
        super.onStart()
        getGrocery()
    }

    override fun addTodoDialog() {
        val dialog = AlertDialog.Builder(activity as MainActivity)
        val view = (activity as MainActivity).layoutInflater.inflate(R.layout.dialog_add_grocery, null)
        dialog.setView(view)
                .setPositiveButton("Add") { _, _ ->
                    insertTodo(TodoEntity(view.edit_text_grocery.text.toString(), false, "Grocery", null))
                }
                .setNegativeButton("Cancel") {dialog, _ ->
                    dialog.dismiss()
                }
        dialog.show()
    }

    override fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = GroceryAdapter(groceryList, { onItemChecked(it) }, { onItemDeleted(it) })
        recycler_view.adapter = adapter
    }

    private fun getGrocery() {
        getTodo("Grocery")
                .subscribe {
                    groceryList.clear()
                    it.forEach { groceryList.add(Todo(it.id, it.todo, it.done, it.type, it.dateTime)) }
                    adapter.notifyDataSetChanged()
                }
    }

    private fun onItemChecked(todo: Todo) {
        if(!recycler_view.isComputingLayout) {
            if(todo.done) updateTodo(todo, false) else updateTodo(todo, true)
        }
    }

    private fun onItemDeleted(selected: List<Int>) {
        deleteTodo(selected, groceryList)
        getGrocery()
        val snackBar = Snackbar.make(coordinator_layout, "${selected.size} item deleted", Snackbar.LENGTH_SHORT)
        snackBar.show()
        snackBar.setAction("UNDO") {
            undoDeleteTodo()
            getGrocery()
        }
    }

}
