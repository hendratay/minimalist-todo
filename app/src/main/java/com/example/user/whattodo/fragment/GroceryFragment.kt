package com.example.user.whattodo.fragment

import android.app.AlertDialog
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.adapter.GroceryAdapter
import com.example.user.whattodo.db.TodoEntity
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.fragment_todo.*

class GroceryFragment: TodoFragment() {

    private lateinit var adapter: GroceryAdapter
    private var groceryList: MutableList<Todo> = ArrayList()

    override fun onStart() {
        super.onStart()
        getGrocery()
    }

    override fun addTodoDialog() {
        val alert = AlertDialog.Builder(activity as MainActivity)
        val groceryEditText = EditText(activity)
        groceryEditText.hint = "Enter Groceries"

        alert.setTitle("Add New Grocery")
        // TODO: Use custom layout
        alert.setView(groceryEditText)

        alert.setPositiveButton("Add") { dialog, _ ->
            insertTodo(TodoEntity(groceryEditText.text.toString(), false, "Grocery", null))
            getGrocery()
            dialog.dismiss()
        }
        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
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
