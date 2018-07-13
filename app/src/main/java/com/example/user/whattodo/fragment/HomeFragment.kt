package com.example.user.whattodo.fragment

import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.adapter.HomeAdapter
import kotlinx.android.synthetic.main.fragment_todo.*

class HomeFragment: TodoFragment() {

    private lateinit var homeAdapter: HomeAdapter
    private var todoType: MutableList<String> = ArrayList()
    private var todoCount: MutableList<Int> = ArrayList()
    private var todoDoneCount: MutableList<Int> = ArrayList()

    override fun onStart() {
        super.onStart()
        getType()
    }

    override fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        homeAdapter = HomeAdapter(todoType, todoCount, todoDoneCount)
        recycler_view.adapter = homeAdapter
    }

    private fun getType() {
        getTodoType()
                .subscribe {
                    todoType.clear()
                    it.forEach { todoType.add(it) }
                    homeAdapter.notifyDataSetChanged()
                    getCount()
                    getDoneCount()
                }
    }

    private fun getCount() {
        for(allType in 0..(todoType.size - 1)) {
            getTodoCount(todoType[allType])
                    .subscribe {
                        todoCount.add(allType, it)
                        homeAdapter.notifyDataSetChanged()
                    }
        }
    }

    private fun getDoneCount() {
        for(allType in 0..(todoType.size - 1)) {
            getTodoDoneCount(todoType[allType])
                    .subscribe {
                        todoDoneCount.add(allType, it)
                        homeAdapter.notifyDataSetChanged()
                    }
        }
    }

}