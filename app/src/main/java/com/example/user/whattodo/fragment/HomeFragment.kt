package com.example.user.whattodo.fragment

import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.adapter.HomeAdapter
import com.example.user.whattodo.model.*
import kotlinx.android.synthetic.main.fragment_todo.*

class HomeFragment: TodoFragment() {

    private lateinit var adapter: HomeAdapter
    private var todoList: MutableList<Todo> = ArrayList()

    override fun setupRecyclerView() {
        adapter = HomeAdapter(todoList)
        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.adapter = adapter
    }

}