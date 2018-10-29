package com.example.user.whattodo.fragment

import android.annotation.SuppressLint
import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.adapter.HomeAdapter
import kotlinx.android.synthetic.main.fragment_todo.*

class HomeFragment: TodoFragment() {

    private lateinit var adapter: HomeAdapter
    private val todoType: MutableList<String> = ArrayList()
    private val todoCount: MutableList<Int> = ArrayList()
    private var todoDoneCount: MutableList<Int> = ArrayList()

    override fun setupRecyclerView() {
        adapter = HomeAdapter(todoType, todoCount, todoDoneCount)
        recycler_view.layoutManager = LinearLayoutManager(requireContext())
        recycler_view.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        getType()
    }

    @SuppressLint("CheckResult")
    private fun getType() {
        getTodoType()
                .subscribe { todo ->
                    todoType.clear()
                    todo.forEach { todoType.add(it) }
                    adapter.notifyDataSetChanged()
                    todoCount()
                    doneTodoCount()
                }
    }

    @SuppressLint("CheckResult")
    private fun todoCount() {
        for(allType in 0..(todoType.size - 1)) {
            getTodoCount(todoType[allType])
                    .subscribe {
                        todoCount.add(allType, it)
                        adapter.notifyDataSetChanged()
                    }
        }
    }

    @SuppressLint("CheckResult")
    private fun doneTodoCount() {
        for(allType in 0..(todoType.size - 1)) {
            getTodoDoneCount(todoType[allType])
                    .subscribe {
                        todoDoneCount.add(allType, it)
                        adapter.notifyDataSetChanged()
                    }
        }
    }

}