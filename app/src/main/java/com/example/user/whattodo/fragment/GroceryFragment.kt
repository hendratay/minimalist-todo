package com.example.user.whattodo.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.adapter.GroceryAdapter
import com.example.user.whattodo.db.TodoEntity
import com.example.user.whattodo.model.Todo
import com.example.user.whattodo.utils.HeaderDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_grocery.*

class GroceryFragment: Fragment() {

    private lateinit var adapter: GroceryAdapter
    private var groceryList: MutableList<Todo> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grocery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupButtonAdd()
        setupRecyclerView()
        getGrocery()
    }

    private fun setupButtonAdd() {
        button_add_grocery.setOnClickListener {
            addGroceryDialog()
        }
    }

    private fun setupRecyclerView() {
        rv_grocery.layoutManager = LinearLayoutManager(activity as MainActivity)
        rv_grocery.addItemDecoration(HeaderDecoration(activity as MainActivity, rv_grocery, R.layout.header_item, "Shopping List"))
        adapter = GroceryAdapter(groceryList)
        rv_grocery.adapter = adapter
    }

    private fun addGroceryDialog() {
        val alert = AlertDialog.Builder(activity as MainActivity)
        val groceryEditText = EditText(activity)
        groceryEditText.hint = "Enter Groceries"

        alert.setTitle("Add New Grocery")
        // TODO: Use custom layout
        alert.setView(groceryEditText)

        alert.setPositiveButton("Add") { dialog, _ ->
            (activity as MainActivity).database.todoDao().insertTodo(TodoEntity(groceryEditText.text.toString(), false, "Grocery", null))
            getGrocery()
            dialog.dismiss()
        }
        alert.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alert.show()
    }

    private fun getGrocery() {
        (activity as MainActivity).database.todoDao().getTodo("Grocery")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    groceryList.clear()
                    for (todo: TodoEntity in it) {
                        groceryList.add(Todo(todo.id, todo.todo, todo.done, todo.type, todo.dateTime))
                    }
                    adapter.notifyDataSetChanged()
                }
    }

}
